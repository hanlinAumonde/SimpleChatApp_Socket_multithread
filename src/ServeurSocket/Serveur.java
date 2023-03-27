package ServeurSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {
    public static void main(String[] args){
        try{
            ServerSocket Serveur = new ServerSocket(10080);
            ClientThread client;
            DataOutputStream outputs_connexion;
            DataInputStream inputs_connexion;
            String clientPseudo;

            while(true){
                //Attendre un client
                Socket clientConnexe = Serveur.accept();
                System.out.println("\nUn nouveau client a connecté ");
                outputs_connexion = new DataOutputStream(clientConnexe.getOutputStream());
                inputs_connexion = new DataInputStream(clientConnexe.getInputStream());

                //Identification
                clientPseudo = inputs_connexion.readUTF();
                while(ClientThread.ListeClients.containsKey(clientPseudo)){
                    outputs_connexion.writeUTF("\nIdentification invalide!");
                    outputs_connexion.flush();
                    clientPseudo = inputs_connexion.readUTF();
                }
                outputs_connexion.writeUTF("\nIdentification valide!");
                System.out.println("Le client ' " + clientPseudo + " ' est bien identifié");
                outputs_connexion.flush();

                //créer un thread pour traiter le conversation avec ce client
                client = new ClientThread(clientConnexe,clientPseudo,inputs_connexion,outputs_connexion);
                ClientThread.ListeClients.put(clientPseudo,client);  //Ajouter le client dans la liste
                client.start();
            }
        }catch(IOException ioException){
            ioException.getStackTrace();
        }
    }
}
