package io.ipolyzos.producers

import io.ipolyzos.models.SensorDomain
import org.apache.pulsar.client.api.PulsarClient

object SensorEventProducer extends App {
  val pulsarClient: PulsarClient = PulsarClient.builder()
    .serviceUrl("pulsar://localhost:6650")
    .build()
    while (true) {
      println(SensorDomain.generate())
    }
}
