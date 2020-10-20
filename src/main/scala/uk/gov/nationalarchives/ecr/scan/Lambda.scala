package uk.gov.nationalarchives.ecr.scan

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset

import cats.effect.IO
import cats.implicits._
import ImageUtils._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

class Lambda() {

  def process(input: InputStream, output: OutputStream): Unit = {
    for {
      logger <- Slf4jLogger.create[IO]
      utils <- ImageUtils()
      repositories <- utils.listRepositories
      images <- utils.describeImages(repositories)
      scans <- utils.scanImage(images).map(_.attempt).sequence
    } yield {
      val (failed, succeeded) = scans.partitionMap(identity)
      output.write(succeeded.toByteArray())
      failed.foreach(err => logger.error(err)(err.getMessage))
      failed.throwIfErrors()
    }
  }.unsafeRunSync()
}
