import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "WorkoutMachine",
    
    libraryDependencies += munit % Test,
    
    libraryDependencies += "io.reactivex" %% "rxscala" % "0.27.0",

    libraryDependencies += "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "2.1.0", 

    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.8.8",
  )

enablePlugins(Compile / doc)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
