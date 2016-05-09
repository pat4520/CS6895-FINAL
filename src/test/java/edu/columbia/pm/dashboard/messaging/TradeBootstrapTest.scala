package edu.columbia.pm.dashboard.messaging

import edu.columbia.pm.dashboard.data.{HoldingKey, Trade}
import edu.columbia.pm.dashboard.helper.TradeBootstrap
import org.json4s._
import org.json4s.jackson.Serialization._
import org.junit.Test

class TradeBootstrapTest {

  implicit val formats = DefaultFormats

  @Test
  def testGetHoldings() = println(TradeBootstrap.getHoldings())

  @Test
  def testTradePublisher() = {
    //val jsonString = write(List(Trade(1L, HoldingKey(1L, 1L), 100L),Trade(2L, HoldingKey(1L, 2L), 100L)))
    val jsonString = write(List(
      Trade(1L, HoldingKey("C3P0", "MSFT"), -300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      //      Trade(1L, HoldingKey("C3P0", "MSFT"), 300L),
      Trade(1L, HoldingKey("C3P0", "IBM"), 500L)))
    println(jsonString)
    new KafkaProducer().send("test-ks-trades", List(jsonString))
  }

}
