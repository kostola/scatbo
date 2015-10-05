// Dependencies
val depScalaj = "org.scalaj" %% "scalaj-http" % "1.1.5"
val depSpray  = "io.spray" %%  "spray-json" % "1.3.2"
val depAkka   = "com.typesafe.akka" %% "akka-actor" % "2.3.12"

// Common settings
lazy val commonSettings = Seq(
  organization := "me.alecosta",
  version      := "0.2.0",
  scalaVersion := "2.11.7"
)

lazy val core = (project in file("core")).
  settings(commonSettings: _*).
  settings(
    name := "scatbo-core",
    libraryDependencies ++= Seq(
      depScalaj,
      depSpray,
      depAkka
    )
  )

lazy val actors = (project in file("actors")).
  settings(commonSettings: _*).
  settings(
    name := "scatbo-actors",
    libraryDependencies ++= Seq(
      depScalaj,
      depSpray,
      depAkka
    )
  ).
  dependsOn(core)

lazy val root = (project in file(".")).
  aggregate(core, actors).
  settings(commonSettings: _*).
  settings(
    name := "scatbo-full",
    libraryDependencies ++= Seq(
      depScalaj,
      depSpray,
      depAkka
    )
  )
