name := "reactive-kafka-sample"

organization in ThisBuild := "sample"

scalaVersion in ThisBuild := ScalaConfig.version

scalacOptions in ThisBuild := ScalaConfig.compilerOptions.value

libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % Versions.akka,

  // Kafka
  "com.typesafe.akka" %% "akka-stream-kafka" % Versions.reactiveKafka,

  // Logging
  "com.typesafe.akka" %% "akka-slf4j" % Versions.akka,
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % Versions.log4j,
  "org.apache.logging.log4j" % "log4j-core" % Versions.log4j,
  "org.apache.logging.log4j" % "log4j-api" % Versions.log4j
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
