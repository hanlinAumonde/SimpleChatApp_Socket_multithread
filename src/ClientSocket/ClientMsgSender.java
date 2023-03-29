package ClientSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

//Thread écriture

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
        Scanner sc = new Scanner(System.in); //lecture bloquante

        try {
            while (isConnect) {
                Msg_Send = sc.nextLine();
                outputs.writeUTF("\n" + Msg_Send);
                outputs.flush();

                if (Msg_Send.startsWith("exit")) {
                    isConnect = false;
                }
            }
            try {
                latch.await();  //Attendant le termination du thread lecture
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            /**
            outputs.writeUTF("\nexit confirmed");
            outputs.flush();

            Cette partie est utilisée pour simuler les trois poignées de main lorsque la connexion TCP est fermée, pour s'assurer que le flux de données client est fermé plus tôt que la fin du serveur, et l'utilisation de countDownlatch était à l'origine utilisée pour assurer la progression normale de la poignée de main à trois voies .
Cependant, des problèmes ont été rencontrés lors du test. La dernière prise de contact lève toujours une IOException côté serveur, ce qui provoque une déconnexion anormale du serveur. Par conséquent, une solution alternative est utilisée, qui consiste à laisser le serveur dormir pendant un certain temps pour assurez-vous que le flux de données client est fermé.

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
