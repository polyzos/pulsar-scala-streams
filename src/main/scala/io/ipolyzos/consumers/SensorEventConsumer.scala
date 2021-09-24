package io.ipolyzos.consumers

import com.sksamuel.pulsar4s.{ConsumerConfig, PulsarClient, Subscription, Topic}
import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.SensorDomain.SensorEvent
import org.apache.pulsar.client.api.{SubscriptionInitialPosition, SubscriptionType}

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
      subscriptionInitialPosition = Some(SubscriptionInitialPosition.Earliest),
      subscriptionType = Some(SubscriptionType.Exclusive)
    )

    val consumerFn = pulsarClient.consumer[SensorEvent](consumerConfig)
    var totalMessageCount = 0
    while (true) {
      consumerFn.receive match {
        case Success(message) =>
          totalMessageCount += 1
          println(s"Total Messages '$totalMessageCount' - Acked Message: ${message.messageId}")
          consumerFn.acknowledge(message.messageId)
        case Failure(exception) =>
          println(s"Failed to receive message: ${exception.getMessage}")
      }
    }
  }
}
