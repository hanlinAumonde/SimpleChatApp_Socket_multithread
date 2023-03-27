package ClientSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class ClientMsgSender extends Thread {
    private boolean isConnect;

    private final CountDownLatch latch;

    private final DataOutputStream outputs;

    public ClientMsgSender(DataOutputStream outputs,CountDownLatch latch) throws IOException {
        this.latch = latch;
        this.isConnect = true;
        this.outputs = outputs;
    }

    @Override
    public void run() {
        String Msg_Send;
        Scanner sc = new Scanner(System.in);

        try {
            while (isConnect) {
                Msg_Send = sc.nextLine();
                outputs.writeUTF("\n" + Msg_Send);
                outputs.flush();

                if (Msg_Send.startsWith("exit")) {
                    isConnect = false;
                }
            }
            // Wait for the "exit valide" message to be received before sending "exit confirmed"
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
            outputs.writeUTF("\nexit confirmed");
            outputs.flush();
             */
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
