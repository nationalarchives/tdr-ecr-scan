package uk.gov.nationalarchives.ecr.scan

import java.io.{InputStream, OutputStream}

import cats.effect.IO
import cats.implicits._
import org.typelevel.log4cats.slf4j.Slf4jLogger
import uk.gov.nationalarchives.ecr.scan.ImageUtils._
import cats.effect.unsafe.implicits.global

class Lambda() {

  def process(input: InputStream, output: OutputStream): Unit = {
    for {
      logger <- Slf4jLogger.create[IO]
      utils <- ImageUtils()
      repositories <- utils.listRepositories
      images <- utils.describeImages(repositories)
      scans <- utils.scanImage(images).map(_.attempt).sequence
      scanMap <- IO(scans.partitionMap(identity))
      (failure, success) = scanMap
      _ <-  IO(output.write(success.toByteArray()))
      _ <- failure.distinct.map(err => logger.error(err)(err.getMessage)).sequence
    } yield ()
  }.unsafeRunSync()
}
