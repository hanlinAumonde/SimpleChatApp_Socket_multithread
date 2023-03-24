package ClientScocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ClientMsgReceptor extends Thread{
    //private Socket ClientSocket;
    private boolean isConnect;

    private final DataInputStream inputs;

    public ClientMsgReceptor(Socket client) throws IOException {
        //this.ClientSocket = client;
        this.isConnect = true;
        this.inputs = new DataInputStream(client.getInputStream());
    }

    @Override
    public void run(){
        String Msg_Receive;

        try{
            while(isConnect){
                Msg_Receive = inputs.readUTF();
                if(Msg_Receive.startsWith("\nexit")){
                   isConnect = false;
                }
                System.out.println(Msg_Receive);
            }

        }catch(SocketException socketException){
            System.out.println("\nErreur! Serveur perdu! InputStream est sur le point d'être fermé");
        }catch(IOException ioException){
            ioException.getStackTrace();
        }finally{
            try{
                inputs.close();
            }catch(IOException ioException){
                ioException.getStackTrace();
            }
        }
    }

}
