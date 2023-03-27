package ClientSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.CountDownLatch;


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

        try {
            while (isConnect) {
                Msg_Receive = inputs.readUTF();
                if (Msg_Receive.startsWith("\nexit valide")) {
                    isConnect = false;
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
