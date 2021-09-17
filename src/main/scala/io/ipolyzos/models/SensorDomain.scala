package io.ipolyzos.models

import java.util.UUID
import scala.util.Random

object SensorDomain {
  private lazy val random = Random
  private lazy val startupTime = System.currentTimeMillis()

  private var sensorIds: Map[Int, (String, Boolean)] = (0 until 10).map(index => index -> (UUID.randomUUID().toString, false)).toMap

  trait Status
  case object Running  extends Status
  case object Starting extends Status
  case object Shutdown extends Status

  case class SensorEvent(sensorId: String,
                         status: String,
                         startupTime: Long,
                         eventTime: Long,
                         reading: Double)

  def generate(): SensorEvent = {
    Thread.sleep(Random.nextInt(500) + 200)
    val index = random.nextInt(sensorIds.size)
    val sensorId = sensorIds.get(index).getOrElse(UUID.randomUUID().toString, false)
    val reading = if (!sensorId._2) {
      sensorIds += (index -> (sensorId._1, true))
      println(s"Starting Sensor - $index")
      SensorEvent(sensorId._1, "Starting", startupTime, System.currentTimeMillis(), 0.0)
    } else {
      val reading = BigDecimal(40 + random.nextGaussian()).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
      SensorEvent(sensorId._1, "Running", startupTime, System.currentTimeMillis(), reading)
    }
    reading
  }
}
