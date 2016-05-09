package edu.columbia.pm.dashboard.helper

import edu.columbia.pm.dashboard.cache.Cache
import edu.columbia.pm.dashboard.messaging.KafkaProducer
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._

object MarketDataBootstrap {

  def getSoDPrices(): String =
    write(Map("MarketData" ->
      Cache.getHoldings(1)
        .collect { case holding => holding.holdingKey.productId -> Cache.prices.getOrElse(holding.holdingKey.productId, 0.0) }
        .groupBy(_._1).map { case (k, v) => v.head }
    ))(DefaultFormats)

  def run() = new KafkaProducer().send("test-ks-prices", List(getSoDPrices()))

  def main(args: Array[String]) = run()

}
