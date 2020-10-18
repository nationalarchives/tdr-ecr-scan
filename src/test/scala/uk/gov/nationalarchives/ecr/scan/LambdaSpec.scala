package uk.gov.nationalarchives.ecr.scan

import java.io.ByteArrayOutputStream

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.{equalTo, okJson, post, urlEqualTo}
import com.github.tomakehurst.wiremock.matching.StringValuePattern
import org.mockito.{ArgumentCaptor, MockitoSugar}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class LambdaSpec extends AnyFlatSpec with Matchers with MockitoSugar {

  val server = new WireMockServer(9001)
  server.stubFor(
    post(urlEqualTo("/"))
      .withHeader("X-Amz-Target", equalTo("AmazonEC2ContainerRegistry_V20150921.DescribeRepositories"))
      .willReturn(okJson("""{
                           |   "nextToken": "string",
                           |   "repositories": [
                           |      {
                           |         "createdAt": 1,
                           |         "encryptionConfiguration": {
                           |            "encryptionType": "string",
                           |            "kmsKey": "string"
                           |         },
                           |         "imageScanningConfiguration": {
                           |            "scanOnPush": true
                           |         },
                           |         "imageTagMutability": "string",
                           |         "registryId": "string",
                           |         "repositoryArn": "string",
                           |         "repositoryName": "string",
                           |         "repositoryUri": "string"
                           |      },
                           |      {
                           |         "createdAt": 1,
                           |         "encryptionConfiguration": {
                           |            "encryptionType": "string",
                           |            "kmsKey": "string"
                           |         },
                           |         "imageScanningConfiguration": {
                           |            "scanOnPush": true
                           |         },
                           |         "imageTagMutability": "string",
                           |         "registryId": "string",
                           |         "repositoryArn": "string",
                           |         "repositoryName": "string",
                           |         "repositoryUri": "string"
                           |      }
                           |   ]
                           |}""".stripMargin))
  )
  server.stubFor(
    post(urlEqualTo("/"))
      .withHeader("x-amz-target", equalTo("AmazonEC2ContainerRegistry_V20150921.DescribeImages"))
      .willReturn(okJson("""{
                           |   "imageDetails": [
                           |      {
                           |         "artifactMediaType": "string",
                           |         "imageDigest": "string",
                           |         "imageManifestMediaType": "string",
                           |         "imagePushedAt": 1,
                           |         "imageScanFindingsSummary": {
                           |            "findingSeverityCounts": {
                           |               "string" : 1
                           |            },
                           |            "imageScanCompletedAt": 1,
                           |            "vulnerabilitySourceUpdatedAt": 1
                           |         },
                           |         "imageScanStatus": {
                           |            "description": "string",
                           |            "status": "string"
                           |         },
                           |         "imageSizeInBytes": 1,
                           |         "imageTags": [ "string" ],
                           |         "registryId": "string",
                           |         "repositoryName": "string"
                           |      }
                           |   ],
                           |   "nextToken": "string"
                           |}""".stripMargin))
  )

  server.stubFor(
    post(urlEqualTo("/"))
      .withHeader("x-amz-target", equalTo("AmazonEC2ContainerRegistry_V20150921.StartImageScan"))
      .willReturn(okJson("""{
                           |   "imageId": {
                           |      "imageDigest": "string",
                           |      "imageTag": "string"
                           |   },
                           |   "imageScanStatus": {
                           |      "description": "string",
                           |      "status": "string"
                           |   },
                           |   "registryId": "string",
                           |   "repositoryName": "string"
                           |}""".stripMargin))
  )

  "it" should "test things" in {
    server.start()
    val stream = mock[ByteArrayOutputStream]
    val captor: ArgumentCaptor[Array[Byte]] = ArgumentCaptor.forClass(classOf[Array[Byte]])
    doNothing.when(stream).write(captor.capture())
    new Lambda().process(null, stream)
    val a: String = captor.getAllValues.get(0).map(_.toChar).mkString
    a
  }

}
