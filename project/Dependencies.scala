import sbt._

object Dependencies {
  lazy val awsUtils =  "uk.gov.nationalarchives.aws.utils" %% "tdr-aws-utils" % "0.1.11"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "2.5.4"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.2"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock" % "2.27.2"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.14.1"
  lazy val log4cats = "io.chrisdavenport" %% "log4cats-core"    % "1.1.1"
  lazy val log4catsSlf4j = "io.chrisdavenport" %% "log4cats-slf4j"   % "1.1.1"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "1.7.30"
}
