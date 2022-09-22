import Dependencies._
import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider

ThisBuild / scalaVersion     := "2.13.9"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "uk.gov.nationalarchives.ecr.scan"

lazy val root = (project in file("."))
  .settings(
    name := "tdr-ecr-scan",
    libraryDependencies ++= Seq(
      awsUtils,
      catsEffect,
      log4cats,
      log4catsSlf4j,
      slf4j,
      mockito % Test,
      wiremock % Test,
      scalaTest % Test
    )
  )

resolvers ++= Seq[Resolver](
  "TDR Releases" at "s3://tdr-releases-mgmt"
)

(assembly / assemblyJarName) := "ecr-scan.jar"

(assembly / assemblyMergeStrategy) := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

(Test / fork) := true
(Test / javaOptions) += s"-Dconfig.file=${sourceDirectory.value}/test/resources/application.conf"
(Test / envVars) := Map("AWS_ACCESS_KEY_ID" -> "test", "AWS_SECRET_ACCESS_KEY" -> "test")
