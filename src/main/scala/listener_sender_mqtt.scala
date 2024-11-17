import akka.actor.{Actor, ActorSystem, Props}
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import rx.lang.scala.{Observable, Subject}

// Messages Akka pour la gestion des événements MQTT
case object Connect
case object Disconnect
case class Subscribe(topic: String)
case class Publish(topic: String, message: String)
case class MessageArrived(topic: String, message: String)

// Acteur MQTT pour gérer la connexion, les abonnements et les messages
class MqttActor(brokerUrl: String, clientId: String) extends Actor {

  val mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence)


  val mqttSubject: Subject[MessageArrived] = Subject()

  mqttClient.setCallback(new MqttCallback {
    override def connectionLost(cause: Throwable): Unit = {
      println(s"Connexion perdue : ${cause.getMessage}")
    }

    override def messageArrived(topic: String, mqttMessage: MqttMessage): Unit = {
      val message = new String(mqttMessage.getPayload)
      println(s"Message reçu sur le topic '$topic' : $message")
      // Émet le message via le sujet Rx
      mqttSubject.onNext(MessageArrived(topic, message))
    }

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      println("Message publié avec succès.")
    }
  })

  // Gestion des messages Akka reçus
  override def receive: Receive = {
    case Connect =>
      mqttClient.connect()
      println("Connecté au broker MQTT.")

    case Disconnect =>
      mqttClient.disconnect()
      println("Déconnecté du broker MQTT.")

    case Subscribe(topic) =>
      if (!mqttClient.isConnected) mqttClient.connect()
      mqttClient.subscribe(topic)
      println(s"Abonné au topic : $topic")

    case Publish(topic, message) =>
      if (!mqttClient.isConnected) mqttClient.connect()
      val mqttMessage = new MqttMessage(message.getBytes)
      mqttClient.publish(topic, mqttMessage)
      println(s"Message publié sur le topic $topic : $message")
  }
}


/**
EXEMPLE d'utilisation de la class mqtt actor

object MqttAkkaListenerSenderApp extends App {
  val appEnv = Option(System.getenv("IP_MQTT")).getOrElse("localhost")
  val brokerUrl = s"tcp://$appEnv:1883"
  val clientId = "AkkaMqttClient"
  val topic = "test/topic"

  // Création du système d'acteurs et de l'acteur MQTT
  val system = ActorSystem("MqttSystem")
  val mqttActor = system.actorOf(Props(new MqttActor(brokerUrl, clientId)), "mqttActor")

  // Cast de l'acteur pour accéder au sujet Rx
  val mqttActorRef = mqttActor.asInstanceOf[MqttActor]

  // Observable pour les messages MQTT
  val observable: Observable[MessageArrived] = mqttActorRef.mqttSubject

  // Exemple d'abonnement à l'observable pour traiter les messages
  observable
    .filter(_.topic == topic) // Filtrer les messages par topic
    .map(msg => s"Message transformé : ${msg.message.toUpperCase}") // Transformation
    .subscribe(msg => println(s"Traitement : $msg"))

  // Connexion, abonnement et attente des messages
  mqttActor ! Connect
  mqttActor ! Subscribe(topic)

  // Ajout d'un hook pour déconnecter et arrêter le système proprement
  sys.addShutdownHook {
    mqttActor ! Disconnect
    system.terminate()
  }
}

*/
