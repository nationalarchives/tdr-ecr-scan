package uk.gov.nationalarchives.ecr.scan

import java.nio.charset.Charset

import cats.effect.IO
import cats.implicits._
import software.amazon.awssdk.services.ecr.model.{DescribeImagesResponse, DescribeRepositoriesResponse, StartImageScanResponse}
import uk.gov.nationalarchives.aws.utils.Clients.ecr
import uk.gov.nationalarchives.aws.utils.ECRUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

import scala.jdk.CollectionConverters._

class ImageUtils() {
  val utils: ECRUtils = ECRUtils(ecr)

  def listRepositories: IO[DescribeRepositoriesResponse] = utils.listRepositories()

  def describeImages(describeRepositoriesResponse: DescribeRepositoriesResponse): IO[List[DescribeImagesResponse]] =
    describeRepositoriesResponse.repositories.asScala.toList
      .map(repository => utils.describeImages(repository.repositoryName())).sequence

  def scanImage(imageResponses: List[DescribeImagesResponse]): List[IO[StartImageScanResponse]] = {
    for {
      response <- imageResponses
      image <- response.imageDetails.asScala.toList
      tag <- image.imageTags.asScala
    } yield utils.startImageScan(EcrImage(image.imageDigest, tag, image.repositoryName))
  }
}

object ImageUtils {
  def apply(): IO[ImageUtils] = IO(new ImageUtils())

  implicit class ImageScanUtils(utils: List[StartImageScanResponse]) {
    def toByteArray(): Array[Byte] = {
      utils.map(_.imageScanStatus().statusAsString()).mkString("\n").getBytes(Charset.defaultCharset())
    }
  }

  implicit class ExceptionUtils(errors: List[Throwable]) {
    def throwIfErrors(): Unit = {
      if(errors.nonEmpty) throw new RuntimeException(errors.map(_.getMessage).mkString("\n"))
    }
  }
}
