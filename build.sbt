name := "kafka-topic-reader"

organization in ThisBuild := "sample"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"
val log4j2Version = "2.5"

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  // Kafka
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-M2",

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % log4j2Version,
  "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
  "org.apache.logging.log4j" % "log4j-api" % log4j2Version
)

fork in run := true

fork in Test := true

parallelExecution in Test := false

enablePlugins(JavaAppPackaging)