package edu.columbia.pm.dashboard.messaging

import edu.columbia.pm.dashboard.helper.MarketDataBootstrap
import org.json4s._
import org.json4s.jackson.Serialization._
import org.junit.Test

class MarketDataBootstrapTest {

  implicit val formats = DefaultFormats


  @Test
  def testGetSoDPrices() = {
    val prices = MarketDataBootstrap.getSoDPrices()
    println(prices)
    val marketData = read[Map[String, Map[String, _]]](prices).getOrElse("MarketData", Map.empty)
    println(marketData)
  }

  @Test
  def testMarketDataPublisher() = {
    val jsonString = write(Map(
      "MarketData" -> Map(
        "MSFT" -> 5.0,
        "IBM" -> 5.0
      )
    ))
    println(jsonString)
    println(MarketDataBootstrap.getSoDPrices())

    new KafkaProducer().send("test-ks-prices", List(jsonString))
  }

}
