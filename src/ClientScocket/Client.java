package ClientScocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class Client {
    public static void main(String[] args){
        try{
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
            outputs_connexion.close();
            inputs_connexion.close();

            //executer les chats
            ClientMsgReceptor ReadMsg = new ClientMsgReceptor(client);
            ClientMsgSender SendMsg = new ClientMsgSender(client);

            ReadMsg.start();
            SendMsg.start();

            ReadMsg.join();
            SendMsg.join();

            client.close();

        }catch(IOException | InterruptedException ex){
            ex.getStackTrace();
        }
    }
}
