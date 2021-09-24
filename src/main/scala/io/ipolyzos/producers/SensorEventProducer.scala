package io.ipolyzos.producers

import com.sksamuel.pulsar4s.{DefaultProducerMessage, EventTime, ProducerConfig, PulsarClient, Topic}
import io.ipolyzos.models.SensorDomain
import io.ipolyzos.models.SensorDomain.SensorEvent
import io.circe.generic.auto._
import com.sksamuel.pulsar4s.circe._
import scala.concurrent.ExecutionContext.Implicits.global

object SensorEventProducer extends App {

  val pulsarClient = PulsarClient("pulsar://localhost:6650")

  val topic = Topic("sensor-events")
  val eventProducer = pulsarClient.producer[SensorEvent](
    ProducerConfig(topic, producerName = Some("sensor-producer"), enableBatching = Some(true), blockIfQueueFull = Some(true))
  )

  (0 until 100) foreach { _ =>
    val sensorEvent = SensorDomain.generate()
    val message = DefaultProducerMessage(
      Some(sensorEvent.sensorId),
      sensorEvent,
      eventTime = Some(EventTime(sensorEvent.eventTime)))
    eventProducer.sendAsync(message)
  }

  // add a shutdown hook to clear the resources
  sys.addShutdownHook(new Thread {
    override def run(): Unit = {
      println("Closing producer and pulsar client..")
      eventProducer.close()
      pulsarClient.close()
    }
  })
}
