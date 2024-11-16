from Phidget22.Phidget import *
from Phidget22.Devices.RFID import *

def main():
    # Créer une instance du lecteur RFID
    rfid = RFID()

    # Définir un écouteur pour l'événement de détection d'étiquette
    def on_tag(rfid, tag, protocol):
        print(f"Étiquette détectée : {tag}")
        print(f"Protocole de la carte : {protocol}")

        # Si le module RFID supporte des opérations supplémentaires, vous pouvez les ajouter ici
        # Par exemple : lecture de données, mais cela dépend des capacités du lecteur et de la carte

    # Définir un écouteur pour l'événement de perte d'étiquette
    def on_tag_lost(rfid, tag, protocol):
        print(f"Étiquette perdue : {tag}")

    # Ajouter les écouteurs d'événements
    rfid.setOnTagHandler(on_tag)
    rfid.setOnTagLostHandler(on_tag_lost)

    # Ouverture du lecteur RFID avec un délai d'attente
    rfid.openWaitForAttachment(5000)  # Attend jusqu'à 5 secondes pour la connexion

    print("Placez une étiquette RFID près du lecteur pour lire ses informations.")
    print("Appuyez sur Ctrl+C pour quitter.")

    try:
        # Boucle pour garder le programme en cours d'exécution
        while True:
            pass  # Boucle infinie jusqu'à ce que l'utilisateur l'interrompe avec Ctrl+C
    except KeyboardInterrupt:
        print("Fermeture du lecteur RFID...")

    # Ferme le lecteur lorsque le programme se termine
    rfid.close()

if __name__ == "__main__":
    main()

