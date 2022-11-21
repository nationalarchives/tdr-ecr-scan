package uk.gov.nationalarchives.ecr.scan

import java.net.URI
import java.nio.charset.Charset

import cats.effect.IO
import cats.implicits._
import com.typesafe.config.ConfigFactory
import software.amazon.awssdk.services.ecr.model.{DescribeImagesResponse, DescribeRepositoriesResponse, StartImageScanResponse}
import uk.gov.nationalarchives.aws.utils.ecr.ECRClients.ecr
import uk.gov.nationalarchives.aws.utils.ECRUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage

import scala.jdk.CollectionConverters._

class ImageUtils() {
  private val ecrClient = ecr(URI.create(ConfigFactory.load.getString("ecr.endpoint")))
  val utils: ECRUtils = ECRUtils(ecrClient)

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
      utils.map(u => s"${u.repositoryName} ${u.imageId.imageTag}").mkString("\n").getBytes(Charset.defaultCharset())
    }
  }
}
