package ClientSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

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

            //executer les chats

            CountDownLatch latch = new CountDownLatch(1);
            ClientMsgReceptor ReadMsg = new ClientMsgReceptor(inputs_connexion,latch);
            ClientMsgSender SendMsg = new ClientMsgSender(outputs_connexion,latch);

            ReadMsg.start();
            SendMsg.start();

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
