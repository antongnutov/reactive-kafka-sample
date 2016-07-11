package sample.kafka.reader

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer.Control
import akka.stream.scaladsl._
import akka.stream.{ActorMaterializer, ClosedShape, SourceShape}
import akka.util.ByteString
import com.typesafe.config.Config
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

/**
  * @author Anton Gnutov
  */
trait ApplicationInit {
  implicit val system: ActorSystem

  val config: Config

  val kafkaSource: Source[ConsumerRecord[String, String], Control]
  val kafkaSink: Sink[ProducerRecord[String, String], _]
  val fileSink: Sink[ByteString, _]
  val loggerSink: Sink[String, _]

  val messageCount: Int
  val outputTopic: String

  def start(): Unit = {

    implicit val materializer = ActorMaterializer()

    import system.dispatcher

    val g = RunnableGraph.fromGraph(GraphDSL.create(kafkaSource.take(messageCount)) { implicit b =>
      source: SourceShape[ConsumerRecord[String, String]] =>
        import GraphDSL.Implicits._

        val bcast = b.add(Broadcast[ConsumerRecord[String, String]](3))
        source ~> bcast.in
        bcast.out(0) ~> Flow[ConsumerRecord[String, String]].map(_.value()) ~> loggerSink
        bcast.out(1) ~> Flow[ConsumerRecord[String, String]].map(m => ByteString(m.value() + "\n")) ~> fileSink
        bcast.out(2) ~> Flow[ConsumerRecord[String, String]].map(cr => new ProducerRecord[String, String](outputTopic, cr.value())) ~> kafkaSink
        ClosedShape
    })

    val control = g.run()
    control.isShutdown.foreach(_ => system.terminate())

    sys.addShutdownHook {
      system.terminate()
    }
  }
}
