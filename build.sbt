name := "reactive-kafka-sample"

organization in ThisBuild := "sample"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.8"
val log4j2Version = "2.5"

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  // Kafka
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.11-M4",

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

// Bash Script config
bashScriptExtraDefines += """addJava "-Dconfig.file=${app_home}/../conf/sample.conf""""
bashScriptExtraDefines += """addJava "-Dlog4j.configurationFile=${app_home}/../conf/log4j2.xml""""

// Bat Script config
batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dconfig.file=%REACTIVE_KAFKA_SAMPLE_HOME%\\conf\\sample.conf"""
batScriptExtraDefines += """set _JAVA_OPTS=%_JAVA_OPTS% -Dlog4j.configurationFile=%REACTIVE_KAFKA_SAMPLE_HOME%\\conf\\log4j2.xml"""
