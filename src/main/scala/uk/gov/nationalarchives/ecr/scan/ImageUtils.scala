package uk.gov.nationalarchives.ecr.scan

import cats.effect.IO
import software.amazon.awssdk.services.ecr.model.{DescribeImagesResponse, DescribeRepositoriesResponse, StartImageScanResponse}
import uk.gov.nationalarchives.aws.utils.Clients.ecr
import uk.gov.nationalarchives.aws.utils.ECRUtils
import uk.gov.nationalarchives.aws.utils.ECRUtils.EcrImage
import scala.jdk.CollectionConverters._
import cats.implicits._

class ImageUtils() {
  val utils: ECRUtils = ECRUtils(ecr)

  def listRepositories: IO[DescribeRepositoriesResponse] = IO.fromTry(utils.listRepositories())

  def describeImages(describeRepositoriesResponse: DescribeRepositoriesResponse): IO[List[DescribeImagesResponse]] =
    describeRepositoriesResponse.repositories.asScala.toList
      .map(repository => IO.fromTry(utils.describeImages(repository.repositoryName()))).sequence

  def scanImages(imageResponses: List[DescribeImagesResponse]): IO[List[StartImageScanResponse]] =
    (for {
      responses <- imageResponses
      image <- responses.imageDetails.asScala
      tag <- image.imageTags.asScala
    } yield IO.fromTry(utils.startImageScan(EcrImage(image.imageDigest, tag, image.repositoryName)))).sequence
}

object ImageUtils {
  def apply(): IO[ImageUtils] = IO(new ImageUtils())
}
