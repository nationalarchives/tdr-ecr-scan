import Dependencies._
import com.amazonaws.auth.{AWSCredentialsProviderChain, DefaultAWSCredentialsProviderChain}
import com.amazonaws.auth.profile.ProfileCredentialsProvider

ThisBuild / scalaVersion     := "2.13.3"
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
      mockito % Test,
      wiremock % Test,
      scalaTest % Test
    )
  )

resolvers ++= Seq[Resolver](
  "TDR Releases" at "s3://tdr-releases-mgmt"
)

assemblyJarName in assembly := "ecr-scan.jar"

fork in Test := true
javaOptions in Test += s"-Dconfig.file=${sourceDirectory.value}/test/resources/application.conf"
