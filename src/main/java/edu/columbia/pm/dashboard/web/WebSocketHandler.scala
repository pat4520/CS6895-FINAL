package edu.columbia.pm.dashboard.web

import edu.columbia.pm.dashboard.cache.Cache
import edu.columbia.pm.dashboard.data.Trade
import edu.columbia.pm.dashboard.messaging.KafkaProducer
import io.vertx.core.Handler
import io.vertx.core.http.ServerWebSocket
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization._

import scala.collection._

class WebSocketHandler extends Handler[ServerWebSocket] {

  val kafkaProducer = new KafkaProducer()
  var peers: mutable.Set[ServerWebSocket] = mutable.Set.empty

  implicit val formats = DefaultFormats

  override def handle(ws: ServerWebSocket) = {
    peers += ws
    ws.closeHandler(new Handler[Void] {
      override def handle(event: Void) = {
        peers -= ws
      }
    })

    kafkaProducer.send("test-ks-trades", List(write(Cache.getHoldingKeys(1).collect { case holdingKey => Trade(0L, holdingKey, 0) })))
  }

  def notifyAll(msg: String) = peers.foreach(ws => ws.writeFinalTextFrame(msg))
}
