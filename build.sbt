import Dependencies._

val buildResolvers = Seq(
  "Typesafe Repo" at "https://repo.typesafe.com/typesafe/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases")

lazy val root = (project in file("."))
  .settings(
    inThisBuild(
      List(
        organization := "io.opentargets",
        scalaVersion := "2.12.12"
      )
    ),
    name := "io-opentargets-etl-literature",
    version := "0.1",
    resolvers ++= buildResolvers,
    libraryDependencies ++= configDeps,
    libraryDependencies ++= loggingDeps,
    libraryDependencies += typeSafeConfig,
    libraryDependencies ++= sparkDeps
  )
