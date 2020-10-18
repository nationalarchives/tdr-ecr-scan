package uk.gov.nationalarchives.ecr.scan

import java.io.{InputStream, OutputStream}
import java.nio.charset.Charset

import cats.effect.IO

class Lambda() {


  def process(input: InputStream, output: OutputStream): Unit =
    (for {
      utils <- ImageUtils()
      repositories <- utils.listRepositories
      images <- utils.describeImages(repositories)
      scan <- utils.scanImages(images)
      output <- IO(output.write(scan.map(_.imageScanStatus.statusAsString()).mkString("\n").getBytes(Charset.defaultCharset)))
    } yield output).unsafeRunSync()
}

