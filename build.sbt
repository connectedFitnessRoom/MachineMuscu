
ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "WorkoutMachine",
    
    libraryDependencies += "io.reactivex" %% "rxscala" % "0.27.0",

    libraryDependencies += "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.5",

    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.8.8",
  )

Global / concurrentRestrictions := Seq(Tags.limitAll(1))
// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
