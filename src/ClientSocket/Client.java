package ClientSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch; //Utilisé pour permettre aux threads de lecture et d'écriture du client de communiquer entre eux

public class Client {
    public static void main(String[] args){
        try{
            //Créer le Socket du client
            Socket client = new Socket("localhost",10080);
            Scanner sc = new Scanner(System.in);
            DataOutputStream outputs_connexion = new DataOutputStream(client.getOutputStream());
            DataInputStream inputs_connexion = new DataInputStream(client.getInputStream());
            String pseudo;

            //verifier que l'identifant est disponible ou non
            System.out.println("\nEntrez votre pseudo : ");
            pseudo = sc.nextLine();
            outputs_connexion.writeUTF(pseudo);
            outputs_connexion.flush();
            String response = inputs_connexion.readUTF();
            do{
                if(response.startsWith("\nIdentification valide!")){
                    break;
                }
                System.out.println("\nPseudo indisponible! Veuillez re-saisir un nouveau pseudo :");
                pseudo = sc.nextLine();
                outputs_connexion.writeUTF(pseudo);
                outputs_connexion.flush();
                response = inputs_connexion.readUTF();

            }while(true);

            //executer les chats

            CountDownLatch latch = new CountDownLatch(1); //Initialisez CountDownLatch et utilisez-le comme l'un des paramètres pour construire deux threads
            ClientMsgReceptor ReadMsg = new ClientMsgReceptor(inputs_connexion,latch);
            ClientMsgSender SendMsg = new ClientMsgSender(outputs_connexion,latch);
            
            //exécuter les threads
            ReadMsg.start();
            SendMsg.start();
            
            //Fermez le socket client après avoir attendu la fin des deux threads
            ReadMsg.join();
            SendMsg.join();

            client.close();

        }catch(IOException ex){
            ex.getStackTrace();
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
