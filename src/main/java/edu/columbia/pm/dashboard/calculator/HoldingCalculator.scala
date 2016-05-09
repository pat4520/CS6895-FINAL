/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.columbia.pm.dashboard.calculator

import edu.columbia.pm.dashboard.cache.Cache
import edu.columbia.pm.dashboard.cache.Cache._
import edu.columbia.pm.dashboard.data.{Holding, Trade}
import edu.columbia.pm.dashboard.helper.SparkLoggingUtil
import edu.columbia.pm.dashboard.messaging.KafkaProducer
import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka._
import org.json4s._
import org.json4s.jackson.Serialization._

object HoldingCalculator {
  implicit val formats = DefaultFormats
  val producer = new KafkaProducer()

  def main(args: Array[String]) {
    SparkLoggingUtil.setStreamingLogLevels()

    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("PortfolioHoldingCalculator")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)

    val holdingsUpdates = lines.flatMap({
      case line: String =>
        if (line contains "tradeId") {
          read[List[Trade]](line).map(trade => trade.holdingKey -> Holding(trade.holdingKey, Option(trade.quantity)))
        } else if (line contains "Predictions") {
          val predictions = read[Map[String, Map[String, _]]](line).getOrElse("Predictions", Map.empty)
          predictions.flatMap {
            case (productId: String, prediction: Double) =>
              Cache.preditions(productId) = prediction
              Cache.getHoldings(productId)
          }.map {
            case holding => holding.holdingKey -> Holding(holding.holdingKey)
          }
        } else if (line contains "MarketData") {
          val marketData = read[Map[String, Map[String, _]]](line).getOrElse("MarketData", Map.empty)
          marketData.flatMap {
            case (productId: String, price: Double) =>
              Cache.prices(productId) = price
              val holdings = Cache.getHoldings(productId)
              holdings.zip(List.fill(holdings.size)(price))
          }.map {
            case (holding, price) => holding.holdingKey -> Holding(holding.holdingKey, Option(0L), holding.quantity.map(_ * price))
          }
        } else {
          None
        }
      case _ => None
    })

    val calculatedHoldings = holdingsUpdates.reduceByKeyAndWindow(
      (curHolding: Holding, newHolding: Holding) =>
        Holding(curHolding.holdingKey, Option(curHolding.quantity.getOrElse(0L) + newHolding.quantity.getOrElse(0L)), Option(newHolding.price.getOrElse(curHolding.price.getOrElse(0.0)))),
      (curHolding: Holding, newHolding: Holding) =>
        Holding(curHolding.holdingKey, Option(curHolding.quantity.getOrElse(0L) - newHolding.quantity.getOrElse(0L)), Option(newHolding.price.getOrElse(curHolding.price.getOrElse(0.0)))),
      Minutes(1420),
      Seconds(2),
      2
    )

    holdingsUpdates.join(calculatedHoldings).map({
      case (key, (curHolding, newHolding)) => key -> newHolding
    }).foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach {
          case (key, holding) =>
            val newHolding = write(List(Map(
              "accountId" -> key.accountId,
              "accountName" -> accounts(key.accountId),
              "productId" -> key.productId,
              "product" -> products(key.productId),
              "quantity" -> holding.quantity,
              "price" -> Cache.prices(key.productId),
              "prediction" -> Cache.preditions(key.productId),
              "holding" -> holding.price,
              "sentiment" -> Cache.sentiments(key.productId)
            )))
            println(newHolding)
            producer.send("test-ks-holdings", List(newHolding))
        }
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
