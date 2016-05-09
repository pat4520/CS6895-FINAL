package edu.columbia.pm.dashboard.helper

import edu.columbia.pm.dashboard.cache.Cache
import edu.columbia.pm.dashboard.data.Trade
import edu.columbia.pm.dashboard.messaging.KafkaProducer
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._

object TradeBootstrap {

  def getHoldings(): String =
    write(Cache.getHoldings(1).collect { case holding => Trade(0L, holding.holdingKey, holding.quantity.getOrElse(0L)) })(DefaultFormats)

  def run() = new KafkaProducer().send("test-ks-trades", List(getHoldings()))

  def main(args: Array[String]) = run()

}
