package sample.kafka.reader

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

object Application extends App with ApplicationInit with ActorSetup {
  implicit val system = ActorSystem("kafka-topic-reader")

  val config = ConfigFactory.load()

  start()
}
