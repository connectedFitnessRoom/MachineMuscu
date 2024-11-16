import akka.actor.{Actor, ActorSystem, Props}
import org.eclipse.paho.client.mqttv3._
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

// Messages Akka pour la gestion des événements MQTT
case object Connect
case object Disconnect
case class Subscribe(topic: String)
case class Publish(topic: String, message: String)
case class MessageArrived(topic: String, message: String)

// Acteur MQTT pour gérer la connexion, les abonnements et les messages
class MqttActor(brokerUrl: String, clientId: String) extends Actor {
  // Configuration du client MQTT
  val mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence)

  // Définition du callback pour gérer les événements MQTT
  mqttClient.setCallback(new MqttCallback {
    override def connectionLost(cause: Throwable): Unit = {
      println(s"Connexion perdue : ${cause.getMessage}")
    }

    override def messageArrived(topic: String, mqttMessage: MqttMessage): Unit = {
      val message = new String(mqttMessage.getPayload)
      println(s"Message reçu sur le topic '$topic' : $message")
      // Envoi du message à l'acteur pour un traitement supplémentaire
      self ! MessageArrived(topic, message)
    }

    override def deliveryComplete(token: IMqttDeliveryToken): Unit = {
      println("Message publié avec succès.")
    }
  })

  // Gestion des messages Akka reçus par l'acteur
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

    case MessageArrived(topic, message) =>
      // Traitement du message reçu (peut être enrichi selon les besoins)
      println(s"Traitement du message reçu sur '$topic' : $message")
  }
}

object MqttAkkaListenerApp extends App {
  val brokerUrl = "tcp://192.168.1.18:1883" // Remplacez par l'URL de votre broker
  val clientId = "AkkaMqttClient"                // Identifiant unique du client
  val topic = "test/topic"                       // Topic d'abonnement

  // Création du système d'acteurs et de l'acteur MQTT
  val system = ActorSystem("MqttSystem")
  val mqttActor = system.actorOf(Props(new MqttActor(brokerUrl, clientId)), "mqttActor")

  // Connexion, abonnement et attente des messages
  mqttActor ! Connect
  mqttActor ! Subscribe(topic)

  // Ajout d'un hook pour déconnecter et arrêter le système proprement
  sys.addShutdownHook {
    mqttActor ! Disconnect
    system.terminate()
  }
}
