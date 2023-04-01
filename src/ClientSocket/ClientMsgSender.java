package ClientSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * La classe ClientMsgSender est un thread pour l'écriture des messages du client.
 */
public class ClientMsgSender extends Thread {

    private boolean isConnect;
    private final CountDownLatch latch;
    private final DataOutputStream outputs;

    /**
     * Constructeur de la classe ClientMsgSender.
     * 
     * @param outputs Le flux de sortie de données pour écrire les messages.
     * @param latch   Le CountDownLatch pour synchroniser les threads de lecture et d'écriture.
     * @throws IOException en cas d'erreur d'E/S.
     */
    public ClientMsgSender(DataOutputStream outputs,CountDownLatch latch) throws IOException {
        this.latch = latch;
        this.isConnect = true;
        this.outputs = outputs;
    }
    
    //getters
    public boolean getIsConnect() {
        return this.isConnect;
    }

    public DataInputStream getOutputs() {
        return this.outputs;
    }
    
    //setter
    public void setIsConnect(boolean status) {
        this.isConnect = status;
    }

    @Override
    public void run() {
        String Msg_Send;
        Scanner sc = new Scanner(System.in); // Lecture bloquante

        try {
            while (getIsConnect()) {
                Msg_Send = sc.nextLine();
                outputs.writeUTF("\n" + Msg_Send);
                outputs.flush();

                if (Msg_Send.startsWith("exit")) {
                    setIsConnect(false);
                }
            }
            try {
                latch.await();  //Attendant le termination du thread lecture
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (SocketException socketException) {
            System.out.println("\nOutputStream est sur le point d'être fermé");
        } catch (IOException ioException) {
            ioException.getStackTrace();
        } finally {
            try {
                outputs.close();
            } catch (IOException ioException) {
                ioException.getStackTrace();
            }
        }
    }
}
