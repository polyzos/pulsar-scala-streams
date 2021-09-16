package io.ipolyzos.producers

import io.ipolyzos.config.AppConfig
import io.ipolyzos.models.SensorDomain
import io.ipolyzos.models.SensorDomain.SensorEvent
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.impl.schema.JSONSchema

object SensorEventProducer extends App {
  val pulsarClient: PulsarClient = PulsarClient.builder()
    .serviceUrl("pulsar://localhost:6650")
    .build()
    while (true) {
      println(SensorDomain.generate())
    }

  val eventProducer = pulsarClient.newProducer(JSONSchema.of(classOf[SensorEvent]))
    .producerName("sensor-event-producer")
    .topic(AppConfig.topicName)
    .enableBatching(true)
    .blockIfQueueFull(true)
    .create()

  (0 until 100) foreach { _ =>
    val sensorEvent = SensorDomain.generate()
    eventProducer.newMessage()
      .key(sensorEvent.sensorId)
      .value(sensorEvent)
      .eventTime(sensorEvent.eventTime)
      .sendAsync()
  }

  // add a shutdown hook to clear the resources
  Runtime.getRuntime.addShutdownHook(new Thread { () =>
    println("Closing producer and pulsar client..")
    eventProducer.close()
    pulsarClient.close()
  })
}
