import sbt._

object Dependencies {
  lazy val awsUtils =  "uk.gov.nationalarchives" %% "tdr-aws-utils" % "0.1.38"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.14"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.13"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock" % "2.27.2"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.17.12"
  lazy val log4cats = "org.typelevel" %% "log4cats-core"    % "2.4.0"
  lazy val log4catsSlf4j = "org.typelevel" %% "log4cats-slf4j"   % "2.4.0"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "2.0.2"
}
