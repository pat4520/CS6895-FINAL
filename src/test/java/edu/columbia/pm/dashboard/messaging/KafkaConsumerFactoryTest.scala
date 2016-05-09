package edu.columbia.pm.dashboard.messaging

import edu.columbia.pm.dashboard.web.WebSocketHandler
import org.junit.Test

class KafkaConsumerFactoryTest {

  @Test
  def testKafkaConsumer(): Unit = {
    KafkaConsumerFactory.startConsumer(new WebSocketHandler())
  }
}
