import sbt._

object Dependencies {
  lazy val awsUtils =  "uk.gov.nationalarchives.aws.utils" %% "tdr-aws-utils" % "0.1.21"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.10"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock" % "2.27.2"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.17.5"
  lazy val log4cats = "org.typelevel" %% "log4cats-core"    % "2.2.0"
  lazy val log4catsSlf4j = "org.typelevel" %% "log4cats-slf4j"   % "2.2.0"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.36"
}
