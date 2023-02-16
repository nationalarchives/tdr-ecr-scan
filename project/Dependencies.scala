import sbt._

object Dependencies {
  lazy val ecrUtils =  "uk.gov.nationalarchives" %% "ecr-utils" % "0.1.73"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.4.7"
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.15"
  lazy val wiremock = "com.github.tomakehurst" % "wiremock" % "2.27.2"
  lazy val mockito = "org.mockito" %% "mockito-scala" % "1.17.12"
  lazy val log4cats = "org.typelevel" %% "log4cats-core"    % "2.5.0"
  lazy val typesafe = "com.typesafe" % "config" % "1.4.2"
  lazy val log4catsSlf4j = "org.typelevel" %% "log4cats-slf4j"   % "2.5.0"
  lazy val slf4j = "org.slf4j" % "slf4j-simple" % "2.0.6"
}
