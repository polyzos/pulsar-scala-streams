name := "pulsar-scala-streams"

version := "0.1"

scalaVersion := "2.12.12"

val pulsar4sVersion = "2.7.3"

lazy val pulsar4s       = "com.sksamuel.pulsar4s" %% "pulsar4s-core" % pulsar4sVersion
lazy val pulsar4sCirce  = "com.sksamuel.pulsar4s" %% "pulsar4s-circe" % pulsar4sVersion

libraryDependencies ++= Seq(
  pulsar4s, pulsar4sCirce
)