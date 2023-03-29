package ServeurSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap; //HashMap sécurisé dans un environnement de mutil-thread

//Thread pour gérer les conversations avec chaque client
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

    //diffusion() permet de diffuser le message à tous les clients connectés au serveur (sauf le client correspondant au thread appelant cette fonction)
    public void diffusion(String Message){
        for(ConcurrentHashMap.Entry<String,ClientThread> entry : ListeClients.entrySet()){
            try{
                if(entry.getValue().pseudo.compareTo(pseudo)!=0) { 
                    //Ne pas diffuser à ce client pour éviter que le programme ne plante en raison d'une déconnexion anormale de ce  client 
                    entry.getValue().outputs.writeUTF(Message);
                    entry.getValue().outputs.flush();
                }
            }catch(SocketException socketException){
                entry.getValue().SocketExceptionHandler();
            }catch(IOException ioException){
                ioException.getStackTrace();
            }
        }
    }
    
    //SocketExceptionHandler() est utilisé pour gérer les déconnexions anormales du client
    public void SocketExceptionHandler(){
        String MsgException = "\nclient " + this.pseudo + " a quitté la conversation";
        this.diffusion(MsgException); //Annoncer aux autres clients que cet client est hors ligne
        ListeClients.remove(pseudo);  //Supprimer le client de la liste des clients
        System.out.println("\nLe connexion du client " + pseudo + " est perdu.");
    }

    @Override
    public void run(){
        String Msg_Receive,Msg_Send;
        boolean isConnect = true;
        try{
            outputs.writeUTF("\nVous avez rejoint la conversation"); //envoyer un message à cet client
            outputs.flush();
            diffusion("\n " + pseudo + " a rejoint la conversation"); //envoyer des messages à d'autres clients
            while(isConnect){ //Cette boucle continue jusqu'à ce qu'elle reçoive un message 'exit' du client.
                Msg_Receive = inputs.readUTF();
                if(!Msg_Receive.startsWith("\nexit")){ //Message normal
                    Msg_Send = "\n" + pseudo + " a dit : " + Msg_Receive;
                    outputs.writeUTF(Msg_Send);
                    outputs.flush();
                    diffusion(Msg_Send);
                }else{  //Message 'exit'
                    Msg_Send = "\nexit valide.";
                    outputs.writeUTF(Msg_Send);
                    outputs.flush();
                    try{ //Veille pendant un certain temps pour s'assurer que le socket client est fermé avant le thread client du serveur
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
    
    //Procédure d'arrêt du thread client
    private void ClientDisconnection() throws IOException{
        ListeClients.remove(pseudo);
        System.out.println("\nLe client " + pseudo + " déconnecte.");
        diffusion("\nclient " + pseudo + " a quitté la conversation");
        inputs.close();
        outputs.close();
        client.close();
    }

}
