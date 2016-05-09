package edu.columbia.pm.dashboard.messaging

import java.util.Properties

import kafka.consumer.{Consumer, ConsumerConfig}
import org.json4s._
import org.json4s.jackson.Serialization._
import org.junit.Test

class KafkaTest {

  implicit val formats = DefaultFormats

  @Test
  def testKafkaProducer(): Unit = {
    val json = write(Map("MarketData" -> Map("IBM" -> 5.0)))
    new KafkaProducer().send("test-kafka-python", List(json))
  }

  @Test
  def testKafkaConsumer(): Unit = {
    val config = {
      val props = new Properties()
      props.put("zookeeper.connect", "localhost:2181")
      props.put("group.id", System.getProperty("user.name"))
      new ConsumerConfig(props)
    }
    val consumer = Consumer.create(config)
    val consumerMap = consumer.createMessageStreams(Map("test-kafka-python" -> 1))
    val stream = consumerMap("test-kafka-python").head

    val it = stream.iterator()
    while (it.hasNext()) {
      println(new String(it.next().message()))
    }

  }

}
