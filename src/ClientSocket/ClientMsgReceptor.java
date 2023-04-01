package ClientSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;

/**
 * La classe ClientMsgReceptor est un thread pour lire les messages du client.
 */
public class ClientMsgReceptor extends Thread{

    private boolean isConnect;
    private final DataInputStream inputs;
    private final CountDownLatch latch;

    /**
     * Constructeur de la classe ClientMsgReceptor.
     * 
     * @param inputs Le flux d'entrée de données pour lire les messages.
     * @param latch Le CountDownLatch pour synchroniser les threads de lecture et d'écriture.
     * @throws IOException en cas d'erreur d'E/S.
     */
    public ClientMsgReceptor(DataInputStream inputs,CountDownLatch latch) throws IOException {
        this.latch = latch;
        this.isConnect = true;
        this.inputs = inputs;
    }
    
    //les getters
    public boolean getIsConnect(){
        return this.isConnect;
    }

    public DataInputStream getInputs(){
        return this.inputs;
    }
    
    //setter
    public void setIsConnect(boolean status){
        this.isConnect = status;
    }

    @Override
    public void run() {
        String Msg_Receive;

        try {  
            //Continuer à boucler pour lire les messages jusqu'à ce que le message de confirmation de sortie envoyé par le serveur soit reçu
            while (getIsConnect()) {
                Msg_Receive = inputs.readUTF();
                if (Msg_Receive.startsWith("\nexit valide")) {
                    setIsConnect(false);
                    latch.countDown(); 
                } else {
                    System.out.println(Msg_Receive);
                }
            }
        } catch (SocketException socketException) {
            System.out.println("\nErreur! Serveur perdu! InputStream est sur le point d'être fermé.\nVeuille entrez n'importe quel caractère pour terminer le processus");
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
