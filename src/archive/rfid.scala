import com.phidget22.{PhidgetException, RFID, RFIDTagEvent, RFIDTagLostEvent,RFIDProtocol}

object RFIDReaderExample {
  def main(args: Array[String]): Unit = {
    val rfid = new RFID()

    try {
      
      rfid.addTagListener((event: RFIDTagEvent) => {
        println(s"Étiquette détectée : ${event.getTag}")
        println(s"Protocole de la carte : ${event.getProtocol}")
      })

      // Définir un écouteur pour l'événement de perte d'étiquette
      rfid.addTagLostListener((event: RFIDTagLostEvent) => {
        println(s"Étiquette perdue : ${event.getTag}")
      })

      // Ouverture du lecteur RFID avec un délai d'attente
      rfid.open(5000) // Attend jusqu'à 5 secondes pour la connexion

      println("Placez une étiquette RFID près du lecteur pour lire ses informations.")
      println("Appuyez sur Ctrl+C pour quitter.")

      while (true) {
        Thread.sleep(100)  
      }
    } catch {
      case e: PhidgetException =>
        println(s"Erreur : ${e.getMessage}")
      case e: InterruptedException =>
        println(s"Erreur d'interruption : ${e.getMessage}")
    } finally {
      try {
        println("Fermeture du lecteur RFID...")
        rfid.close()
      } catch {
        case e: PhidgetException =>
          println(s"Erreur lors de la fermeture du lecteur : ${e.getMessage}")
      }
    }
  }
}
