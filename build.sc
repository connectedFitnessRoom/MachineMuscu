import mill._, scalalib._

object WorkoutMachine extends ScalaModule {
  def scalaVersion = "2.13.12"
  def ivyDeps = Agg(
    ivy"io.reactivex::rxscala:0.27.0",
    ivy"org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5",
    ivy"com.typesafe.akka::akka-actor:2.8.8"
  )

  def mainClass = Some("sensors.SensorsManagement")

}

