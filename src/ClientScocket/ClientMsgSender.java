package ClientScocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientMsgSender extends Thread {
    private boolean isConnect;

    private final DataOutputStream outputs;

    public ClientMsgSender(Socket client) throws IOException {
        this.isConnect = true;
        this.outputs = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run(){
        String Msg_Send;
        Scanner sc = new Scanner(System.in);

        try{
            while(isConnect){
                Msg_Send = sc.nextLine();
                if(Msg_Send.startsWith("\nexit")){
                    isConnect = false;
                }
                outputs.writeUTF(Msg_Send);
                outputs.flush();
            }
        }catch(SocketException socketException){
            System.out.println("\nErreur! Serveur perdu! OutputStream est sur le point d'être fermé");
        }catch(IOException ioException){
            ioException.getStackTrace();
        }finally{
            try{
                outputs.close();
            }catch(IOException ioException){
                ioException.getStackTrace();
            }
        }
    }
}
