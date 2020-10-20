package uk.gov.nationalarchives.ecr.scan

import java.io.{InputStream, OutputStream}

import cats.data.{EitherT, OptionT}
import cats.effect.IO
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import uk.gov.nationalarchives.ecr.scan.ImageUtils._

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
