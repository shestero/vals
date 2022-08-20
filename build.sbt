ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / scalaVersion := "3.1.3"
ThisBuild / organization := "org.shestero.vals"

lazy val root = (project in file("."))
  .settings(
    name := "vals",
    idePackagePrefix := Some("org.shestero.vals")
  )


val catsVersion = "2.7.0"
val monixVersion = "3.4.0"

libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion
libraryDependencies += "io.monix" %% "monix" % monixVersion
