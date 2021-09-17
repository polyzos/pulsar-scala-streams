package io.ipolyzos.consumers

import com.sksamuel.pulsar4s.{ConsumerConfig, PulsarClient, Subscription, Topic}
import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.SensorDomain.SensorEvent
import org.apache.pulsar.client.api.SubscriptionInitialPosition

import java.util.concurrent.atomic.AtomicInteger
import scala.util.{Failure, Success}

object SensorEventConsumer {
  import io.circe.generic.auto._
  import com.sksamuel.pulsar4s.circe._

  def main(args: Array[String]): Unit = {
    val pulsarClient = PulsarClient("pulsar://localhost:6650")

    val topic = Topic(AppConfig.topicName)
    val consumerConfig = ConsumerConfig(
      Subscription("sensor-event-subscription"),
      Seq(topic),
      consumerName = Some("sensor-event-consumer"),
      subscriptionInitialPosition = Some(SubscriptionInitialPosition.Earliest)
    )


    val consumerFn = pulsarClient.consumer[SensorEvent](consumerConfig)

    // add a shutdown hook to clear the resources
    sys.addShutdownHook(new Thread {
      override def run(): Unit = {
        println("Closing producer and pulsar client..")
        consumerFn.close()
        pulsarClient.close()
      }
    })

    val totalMessageCount = new AtomicInteger()
    while (true) {
      consumerFn.receive match {
        case Success(message) =>
          val count = totalMessageCount.getAndIncrement()
          println(s"Total Messages '$count' - Acked Message: ${message.messageId}")
          consumerFn.acknowledge(message.messageId)
        case Failure(exception) =>
          println(s"Failed to receive message: ${exception.getMessage}")
      }
    }
  }
}
