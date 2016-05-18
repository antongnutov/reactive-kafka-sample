package sample.kafka.reader

import akka.actor.{ActorLogging, Props}
import akka.event.Logging
import akka.stream.actor.ActorSubscriberMessage.{OnComplete, OnError, OnNext}
import akka.stream.actor.{ActorSubscriber, RequestStrategy, WatermarkRequestStrategy}

/**
  * @author Anton Gnutov
  */
class LoggerSubscriberActor(level: String) extends ActorSubscriber with ActorLogging {

  val logLevel: Logging.LogLevel = Logging.levelFor(level).getOrElse(Logging.DebugLevel)

  override protected def requestStrategy: RequestStrategy = WatermarkRequestStrategy(32768)

  override def receive: Receive = {
    case OnNext(msg: String) =>
      log.log(logLevel, msg)
    case OnError(cause) =>
      log.error(cause.getMessage)
    case OnComplete =>
      log.log(logLevel, "Completed")
      context.stop(self)
  }
}

object LoggerSubscriberActor {
  def props(level: String) = Props(classOf[LoggerSubscriberActor], level)
}