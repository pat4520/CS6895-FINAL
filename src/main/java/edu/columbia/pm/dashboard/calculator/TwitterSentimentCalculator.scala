package edu.columbia.pm.dashboard.calculator

import java.util.Properties

import com.ibm.watson.developer_cloud.alchemy.v1.AlchemyLanguage
import edu.columbia.pm.dashboard.helper.SparkLoggingUtil
import edu.columbia.pm.dashboard.cache.Cache
import edu.columbia.pm.dashboard.cache.Cache._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.twitter._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder

import scala.collection.mutable.ListBuffer
import scala.collection.JavaConversions._

object TwitterSentimentCalculator {

  var alchemyService: AlchemyLanguage = _

  def main(args: Array[String]) {
    SparkLoggingUtil.setStreamingLogLevels()

    val cb = new ConfigurationBuilder
    cb.setOAuthConsumerKey(args(0))
    cb.setOAuthConsumerSecret(args(1))
    cb.setOAuthAccessToken(args(2))
    cb.setOAuthAccessTokenSecret(args(3))
    val authorization = new OAuthAuthorization(cb.build)

    alchemyService = new AlchemyLanguage()
    alchemyService.setApiKey(args(4))

    val productId = args(5)

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("TwitterSentimentCalculator")

    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val productTweets = TwitterUtils.createStream(ssc, Option(authorization), List(productId))
    val sectorTweets = TwitterUtils.createStream(ssc, Option(authorization), List(sectors(productId)))
    val marketTweets = TwitterUtils.createStream(ssc, Option(authorization), List("NYSE", "Bloomberg"))

    val productSentiment = productTweets.map { t => {
      productId -> detectSentiment(t.getText)
    }
    }
    val sectorSentiment = sectorTweets.map { t => {
      productId -> detectSentiment(t.getText)
    }
    }
    val marketSentiment = marketTweets.map { t => {
      productId -> detectSentiment(t.getText)
    }
    }

    productSentiment.join(sectorSentiment).join(marketSentiment).foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach {
          case (_, ((psVal, ssVal), msVal)) =>
            val sentiment =
              if (psVal == 0) {
                psVal + ssVal + msVal / 3
              } else if (psVal < 3 && (ssVal > 3 && msVal > 3)) {
                psVal
              } else if (psVal > 3 && (ssVal < 3 && msVal < 3)) {
                psVal
              } else {
                psVal + ssVal + msVal / 3
              }

            sentiments(productId) = sentiment.toString.toDouble
        }
      })
    })

    ssc.start()
    ssc.awaitTermination()

  }

  def detectSentiment(message: String) =
    alchemyService.getSentiment(Map(AlchemyLanguage.TEXT -> message))
      .execute().getSentiment.getScore

}