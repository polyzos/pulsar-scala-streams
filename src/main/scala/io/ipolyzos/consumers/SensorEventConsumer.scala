package io.ipolyzos.consumers

import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.SensorDomain.SensorEvent
import org.apache.pulsar.client.api.{Consumer, Message, PulsarClient, SubscriptionInitialPosition}
import org.apache.pulsar.client.impl.schema.JSONSchema

import scala.util.{Failure, Success, Try}

object SensorEventConsumer {

  def main(args: Array[String]): Unit = {
    val pulsarClient: PulsarClient = PulsarClient.builder()
      .serviceUrl("pulsar://localhost:6650")
      .build()


    val consumer: Consumer[SensorEvent] = pulsarClient.newConsumer(JSONSchema.of(classOf[SensorEvent]))
      .consumerName("sensor-event-consumer")
      .topic(AppConfig.topicName)
      .subscriptionName("sensor-event-subscription")
      .subscriptionInitialPosition(SubscriptionInitialPosition.Earliest)
      .subscribe()

    var messageCount = 0
//    Runtime.getRuntime.addShutdownHook(new Thread { () =>
//      println("Closing producer and pulsar client..")
//      consumer.close()
//      pulsarClient.close()
//    })

    while(true) {
      println("Starting")
      val message: Message[SensorEvent] = consumer.receive()
      println("no?")
      Try(consumer.acknowledge(message)) match {
        case Success(_) =>
          messageCount += 1
          println(s"Acked message [${message.getMessageId}] - Total messages acked so far ${}")
        case Failure(_) =>
          println(s"Failed to ack - ${message.getMessageId}")
          consumer.negativeAcknowledge(message)
      }
    }
  }
}
