
from Phidget22.Phidget import *
from Phidget22.Devices.RFID import *

def main():
    # Créer une instance du lecteur RFID
    rfid = RFID()

    # Définir un écouteur pour l'événement de détection d'étiquette
    def on_tag(rfid, tag):
        print(f"Étiquette détectée : {tag}")

    # Définir un écouteur pour l'événement de perte d'étiquette
    def on_tag_lost(rfid, tag):
        print(f"Étiquette perdue : {tag}")

    # Ajouter les écouteurs d'événements
    rfid.setOnTagHandler(on_tag)
    rfid.setOnTagLostHandler(on_tag_lost)

    # Ouverture du lecteur RFID avec un délai d'attente
    rfid.openWaitForAttachment(5000)  # Délai en millisecondes (ici, 5 secondes)

    print("Placez une étiquette RFID près du lecteur pour la détecter.")
    print("Appuyez sur Ctrl+C pour quitter.")

    try:
        # Boucle pour garder le programme en cours d'exécution
        while True:
            pass  # Boucle infinie jusqu'à ce que l'utilisateur l'interrompe avec Ctrl+C
    except KeyboardInterrupt:
        print("Fermeture du lecteur RFID...")

    # Fermez le lecteur lorsque le programme se termine
    rfid.close()
    
if __name__ == "__main__":
    main()

