name := "listener_sender_mqtt"

version := "0.1"

scalaVersion := "2.11.12" 
// Dépendances
libraryDependencies ++= Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.6.20",

  // Eclipse Paho MQTT client
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.5", 

 //RxScala
  "io.reactivex" %% "rxscala" % "0.27.0"
)

enablePlugins(Compile / doc)

javacOptions ++= Seq("-target", "1.8", "-source", "1.8")
