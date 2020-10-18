import sbt._

object Dependencies {
  lazy val awsUtils =  "uk.gov.nationalarchives.aws.utils" %% "tdr-aws-utils" % "0.1.5-SNAPSHOT"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "2.2.0"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock" % "2.27.2"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.14.1"
}
