package ClientSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

//Thread de lecture des messages du client

public class ClientMsgReceptor extends Thread{
    //private Socket ClientSocket;
    private boolean isConnect;
    private final DataInputStream inputs;

    private final CountDownLatch latch;

    public ClientMsgReceptor(DataInputStream inputs,CountDownLatch latch) throws IOException {
        this.latch = latch;
        this.isConnect = true;
        this.inputs = inputs;
    }

    @Override
    public void run() {
        String Msg_Receive;

        try {  //Continuer à boucler pour lire les messages jusqu'à ce que le message de confirmation de sortie envoyé par le serveur soit reçu
            while (isConnect) {
                Msg_Receive = inputs.readUTF();
                if (Msg_Receive.startsWith("\nexit valide")) {
                    isConnect = false;
                    latch.countDown(); 
                    /**
                    Le thread de lecture appelle la fonction countDown pour faire passer le compteur de la valeur initiale 1 à 0, de sorte que le thread d'écriture qui appelle await soit déverrouillé et continue à s'exécuter. 
                    Cela garantit que les deux threads sont fermés en même temps que possible
                    */
                } else {
                    System.out.println(Msg_Receive);
                }
            }
        } catch (SocketException socketException) {
            System.out.println("\nErreur! Serveur perdu! InputStream est sur le point d'être fermé.\nVeuille entrez n'importe quel caractère pour terminer le processus");
            /**
            Write Thread utilise un scanner, qui est une lecture bloquante, donc des caractères d'entrée supplémentaires sont nécessaires pour continuer l'exécution et déclencher une exception de socket
            */
        } catch (IOException ioException) {
            ioException.getStackTrace();
        } finally {
            try {
                inputs.close();
            } catch (IOException ioException) {
                ioException.getStackTrace();
            }
        }
    }

}
