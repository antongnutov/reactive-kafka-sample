package sample.kafka.reader

import java.io.File
import java.nio.file.StandardOpenOption._

import akka.kafka.scaladsl.Consumer.Control
import akka.kafka.scaladsl.{Consumer, Producer}
import akka.kafka.{ConsumerSettings, ProducerSettings, Subscriptions}
import akka.stream.scaladsl.{FileIO, Sink, Source}
import akka.util.ByteString
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}

import scala.collection.JavaConverters._
import scala.concurrent.duration._

/**
  * @author Anton Gnutov
  */
trait ActorSetup {
  self: ApplicationInit =>

  lazy val fileSink: Sink[ByteString, _] = {
    if (config.hasPath("output.file.name")) {
      val name = config.getString("output.file.name")
      FileIO.toPath(new File(name).toPath, Set(WRITE, CREATE, TRUNCATE_EXISTING))
    } else {
      Sink.ignore
    }
  }

  lazy val loggerSink: Sink[String, _] = {
    if (config.hasPath("output.logger.level")) {
      val level = config.getString("output.logger.level")
      Sink.actorSubscriber(LoggerSubscriberActor.props(level))
    } else {
      Sink.ignore
    }
  }

  lazy val kafkaSource: Source[ConsumerRecord[String, String], Control] = {
    val cfg = config.getConfig("input.kafka")
    val bootStrapServers = cfg.getStringList("bootstrap-servers").asScala.toList
    val topics = cfg.getStringList("topics").asScala.toSet
    val group = cfg.getString("group")
    val commit = cfg.getBoolean("commit")
    val start = cfg.getString("start-from")
    val fetchBytes = cfg.getInt("fetch.bytes")

    val count = cfg.getLong("message.count")
    val filter = cfg.getString("message.filter")

    val consumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers(bootStrapServers.mkString(","))
      .withGroupId(group)
      .withProperty(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, fetchBytes.toString)
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, start)
      .withProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, commit.toString)
      .withDispatcher("pinned-dispatcher")
      .withPollInterval(20.millis)
      .withPollTimeout(50.millis)

    val src = Consumer.plainSource(consumerSettings, Subscriptions.topics(topics))
    val filtered = if (filter.nonEmpty) src.filter(_.value().contains(filter)) else src
    if (count > 0) filtered.take(count) else filtered
  }

  lazy val kafkaSink: Sink[ProducerRecord[String, String], _] = {
    if (config.hasPath("output.kafka.bootstrap-servers")) {
      val cfg = config.getConfig("output.kafka")
      val bootStrapServers = cfg.getStringList("bootstrap-servers").asScala
      val compressionType = cfg.getString("producer.compression.type")
      val batchSize = cfg.getInt("producer.batch.size")
      val lingerMs = cfg.getInt("producer.linger.ms")
      val bufferMemory = cfg.getLong("producer.buffer.memory")

      val producerSettings = ProducerSettings(system, new StringSerializer, new StringSerializer)
        .withBootstrapServers(bootStrapServers.mkString(","))
        .withProperty(ProducerConfig.BATCH_SIZE_CONFIG, batchSize.toString)
        .withProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory.toString)
        .withProperty(ProducerConfig.LINGER_MS_CONFIG, lingerMs.toString)
        .withProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType)
      Producer.plainSink(producerSettings)
    } else {
      Sink.ignore
    }
  }

  lazy val outputTopic: String = if (config.hasPath("output.kafka.topic")) {
    config.getString("output.kafka.topic")
  } else {
    ""
  }
}
