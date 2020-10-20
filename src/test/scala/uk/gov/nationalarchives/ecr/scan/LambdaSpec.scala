package uk.gov.nationalarchives.ecr.scan

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.mockito.ArgumentMatchers.any
import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.io.Source.fromResource

class LambdaSpec extends AnyFlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  override def beforeEach(): Unit = {
    server.resetAll()
  }

  val server = new WireMockServer(9001)
  server.start()

  def stubDescribeRepositoriesResponse(fileName: String) = {
    server.stubFor(
      post(urlEqualTo("/"))
        .withHeader("X-Amz-Target", equalTo("AmazonEC2ContainerRegistry_V20150921.DescribeRepositories"))
        .willReturn(okJson(fromResource(s"json/$fileName.json").mkString))
    )
  }

  def stubDescribeImageResponse(repositoryName: String) = {
    val body: String = s"""{"repositoryName":"$repositoryName"}"""
    server.stubFor(
      post(urlEqualTo("/"))
        .withHeader("X-Amz-Target", equalTo("AmazonEC2ContainerRegistry_V20150921.DescribeImages"))
        .withRequestBody(binaryEqualTo(body.getBytes(Charset.defaultCharset())))
        .willReturn(okJson(fromResource(s"json/describe_images_response.json").mkString.replace("$repositoryName", repositoryName)))
    )
  }

  def stubStartImageScanResponse(repositoryName: String) = {
    val body = s"""{"repositoryName":"$repositoryName","imageId":{"imageDigest":"$repositoryName-digest","imageTag":"$repositoryName-latest"}}"""
    server.stubFor(
      post(urlEqualTo("/"))
        .withHeader("X-Amz-Target", equalTo("AmazonEC2ContainerRegistry_V20150921.StartImageScan"))
        .withRequestBody(binaryEqualTo(body.getBytes(Charset.defaultCharset())))
        .willReturn(okJson(fromResource(s"json/start_image_scan_response.json").mkString.replace("$repositoryName", repositoryName)))
    )
  }

  def stubFailedImageScanResponse(repositoryName: String) = {
    val body = s"""{"repositoryName":"$repositoryName","imageId":{"imageDigest":"$repositoryName-digest","imageTag":"$repositoryName-latest"}}"""
    server.stubFor(
      post(urlEqualTo("/"))
        .withHeader("X-Amz-Target", equalTo("AmazonEC2ContainerRegistry_V20150921.StartImageScan"))
        .withRequestBody(binaryEqualTo(body.getBytes(Charset.defaultCharset())))
        .willReturn(badRequest())
    )
  }

  "the process method" should "return the correct status for a single repository" in {
    val repositoryName = "repository1of1"
    stubDescribeRepositoriesResponse("describe_repositories_one_repo")
    stubDescribeImageResponse(repositoryName)
    stubStartImageScanResponse(repositoryName)
    val stream = mock[ByteArrayOutputStream]
    val captor: ArgumentCaptor[Array[Byte]] = ArgumentCaptor.forClass(classOf[Array[Byte]])
    doNothing.when(stream).write(captor.capture())
    new Lambda().process(null, stream)
    val output: String = captor.getAllValues.get(0).map(_.toChar).mkString
    output should equal(s"$repositoryName-status")
  }

  "the process method" should "return the correct status for a multiple repositories" in {
    val repositoryNames = List("repository1of4","repository2of4","repository3of4","repository4of4")
    stubDescribeRepositoriesResponse("describe_repositories_four_repos")
    repositoryNames.foreach(repositoryName => {
      stubDescribeImageResponse(repositoryName)
      stubStartImageScanResponse(repositoryName)
    })
    val stream = mock[ByteArrayOutputStream]
    val captor: ArgumentCaptor[Array[Byte]] = ArgumentCaptor.forClass(classOf[Array[Byte]])
    doNothing.when(stream).write(captor.capture())
    new Lambda().process(null, stream)
    val output: String = captor.getAllValues.get(0).map(_.toChar).mkString
    output should equal(repositoryNames.map(name => s"$name-status").mkString("\n"))
  }

  "the process method" should "return the correct status when one image scan fails" in {
    val repositoryNames = List("repository1of4","repository2of4","repository3of4","repository4of4")
    stubDescribeRepositoriesResponse("describe_repositories_four_repos")
    repositoryNames.foreach(stubDescribeImageResponse)
    repositoryNames.tail.foreach(stubStartImageScanResponse)
    stubFailedImageScanResponse(repositoryNames.head)
    val stream = mock[ByteArrayOutputStream]
    val captor: ArgumentCaptor[Array[Byte]] = ArgumentCaptor.forClass(classOf[Array[Byte]])
    doNothing.when(stream).write(captor.capture())
    new Lambda().process(null, stream)
    val output: String = captor.getAllValues.get(0).map(_.toChar).mkString
    output should equal(repositoryNames.tail.map(name => s"$name-status").mkString("\n"))
  }
}
