name := "pulsar-scala-streams"

version := "0.1"

scalaVersion := "2.12.12"

val pulsarVersion = "2.8.0"

lazy val pulsarClientOriginal = "org.apache.pulsar" % "pulsar-client-original" % pulsarVersion

libraryDependencies ++= Seq(
  pulsarClientOriginal
)