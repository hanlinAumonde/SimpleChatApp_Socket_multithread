package ServeurSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe Serveur permettant de créer un serveur de chat.
 */
public class Serveur {
    public static void main(String[] args){
        try{
            // Création d'un serveur socket sur le port 10080
            ServerSocket Serveur = new ServerSocket(10080);
            ClientThread client;
            DataOutputStream outputs_connexion;
            DataInputStream inputs_connexion;
            String clientPseudo;

            while(true){
                // Attendre qu'un client se connecte
                Socket clientConnexe = Serveur.accept();
                System.out.println("\nUn nouveau client a connecté ");
                outputs_connexion = new DataOutputStream(clientConnexe.getOutputStream());
                inputs_connexion = new DataInputStream(clientConnexe.getInputStream());

                // Identification du client
                clientPseudo = inputs_connexion.readUTF();
                // Vérification de l'unicité du pseudo
                while(ClientThread.ListeClients.containsKey(clientPseudo)){
                    outputs_connexion.writeUTF("\nIdentification invalide!");
                    outputs_connexion.flush();
                    clientPseudo = inputs_connexion.readUTF();
                }
                outputs_connexion.writeUTF("\nIdentification valide!");
                System.out.println("Le client ' " + clientPseudo + " ' est bien identifié");
                outputs_connexion.flush();

                // Création d'un thread pour gérer la conversation avec ce client
                client = new ClientThread(clientConnexe,clientPseudo,inputs_connexion,outputs_connexion);
                ClientThread.ListeClients.put(clientPseudo,client);  //Ajouter le client dans la liste
                client.start();
            }
        }catch(IOException ioException){
            ioException.getStackTrace();
        }
    }
}
