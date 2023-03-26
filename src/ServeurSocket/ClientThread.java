package ServeurSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap; //HashMap utilise dans un environnement de mutil-thread

public class ClientThread extends Thread{
    private final String pseudo; //Identification d'un client
    private final Socket client;
    public static ConcurrentHashMap<String, ClientThread> ListeClients = new ConcurrentHashMap<>(); //Map static pour tous les clients

    //Les flux de données d'un client
    private final DataInputStream inputs;
    private final DataOutputStream outputs;

    public ClientThread(Socket client,String pseudo,DataInputStream inputs,DataOutputStream outputs) throws IOException {
        this.client = client;
        this.pseudo = pseudo;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public void diffusion(String Message){
        for(ConcurrentHashMap.Entry<String,ClientThread> entry : ListeClients.entrySet()){
            try{
                entry.getValue().outputs.writeUTF(Message);
                entry.getValue().outputs.flush();
            }catch(SocketException socketException){
                entry.getValue().SocketExceptionHandler();
            }catch(IOException ioException){
                ioException.getStackTrace();
            }
        }
    }

    public void SocketExceptionHandler(){
        String MsgException = "\nclient " + this.pseudo + " a quitté la conversation";
        this.diffusion(MsgException);
        ListeClients.remove(pseudo);
        System.out.println("\nLe connexion du client " + pseudo + " est perdu.");
    }

    @Override
    public void run(){
        String Msg_Receive,Msg_Send;
        boolean isConnect = true;
        try{
            diffusion("\n " + pseudo + " a rejoint la conversation");
            while(isConnect){
                Msg_Receive = inputs.readUTF();
                if(!Msg_Receive.startsWith("\nexit")){
                    Msg_Send = "\n" + pseudo + " a dit : " + Msg_Receive;
                    diffusion(Msg_Send);
                }else{
                    Msg_Send = "\nexit valide.";
                    outputs.writeUTF(Msg_Send);
                    outputs.flush();
                    try{
                        Thread.sleep(200);
                        isConnect = false;
                    }catch(InterruptedException interruptedException){
                        interruptedException.getStackTrace();
                    }
                }
            }
            ClientDisconnection();

        }catch(SocketException socketException){
            SocketExceptionHandler();
        }catch(IOException ioException){
            ioException.getStackTrace();
        }
    }

    private void ClientDisconnection() throws IOException{
        ListeClients.remove(pseudo);
        System.out.println("\nLe client " + pseudo + " déconnecte.");
        diffusion("\nclient " + pseudo + " a quitté la conversation");
        inputs.close();
        outputs.close();
        client.close();
    }

}
