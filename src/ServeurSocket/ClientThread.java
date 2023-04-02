package ServeurSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap; //HashMap sécurisé dans un environnement de mutil-thread

/**
 * Classe ClientThread, étend Thread pour gérer les clients connectés au serveur de chat.
 */
public class ClientThread extends Thread{
    private final String pseudo; 
    private final Socket client;

    // Map static pour stocker tous les clients
    public static ConcurrentHashMap<String, ClientThread> ListeClients = new ConcurrentHashMap<>(); 

    private final DataInputStream inputs;
    private final DataOutputStream outputs;

    /**
     * Constructeur pour la classe ClientThread.
     *
     * @param client Le socket client
     * @param pseudo Le pseudo du client
     * @param inputs Le flux d'entrée pour lire les messages du client
     * @param outputs Le flux de sortie pour envoyer des messages au client
     * @throws IOException En cas d'erreur d'entrée/sortie
     */
    public ClientThread(Socket client,String pseudo,DataInputStream inputs,DataOutputStream outputs) throws IOException {
        this.client = client;
        this.pseudo = pseudo;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    // Les getters pour les attributs de la classe
    public Socket getClient(){
        return this.client;
    }

    public String getPseudo(){
        return this.pseudo;
    }

    public DataInputStream getInputs(){
        return this.inputs;
    }

    public DataOutputStream getOutputs(){
        return this.outputs;
    }

    /**
     * Fonction pour diffuser un message à tous les clients connectés, sauf l'expéditeur.
     *
     * @param Message Le message à diffuser
     */
    public void diffusion(String Message){
        for(ConcurrentHashMap.Entry<String,ClientThread> entry : ListeClients.entrySet()){
            try{
                if(entry.getValue().getPseudo().compareTo(this.getPseudo())!=0) {  
                    entry.getValue().getOutputs().writeUTF(Message);
                    entry.getValue().getOutputs().flush();
                }
            }catch(SocketException socketException){
                entry.getValue().SocketExceptionHandler();
            }catch(IOException ioException){
                ioException.getStackTrace();
            }
        }
    }
    
    /**
     * Fonction pour traiter les exceptions de socket.
     */
    public void SocketExceptionHandler(){
        String MsgException = "\nclient " + this.pseudo + " a quitté la conversation";
        this.diffusion(MsgException); 
        ListeClients.remove(pseudo);  
        System.out.println("\nLe connexion du client " + pseudo + " est perdu.");
    }

    /**
     * La méthode run, exécutée lorsqu'un nouveau thread est lancé.
     */
    @Override
    public void run(){
        String Msg_Receive,Msg_Send;
        boolean isConnect = true;
        try{
            outputs.writeUTF("\nVous avez rejoint la conversation"); 
            outputs.flush();
            diffusion("\n " + pseudo + " a rejoint la conversation"); 
            while(isConnect){
                Msg_Receive = inputs.readUTF();
                if(!Msg_Receive.startsWith("\nexit")){ 
                    Msg_Send = "\n" + pseudo + " a dit : " + Msg_Receive;
                    outputs.writeUTF(Msg_Send);
                    outputs.flush();
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
    
    /**
     * Fonction pour gérer la déconnexion d'un client.
     *
     * @throws IOException En cas d'erreur d'entrée/sortie
     */
    private void ClientDisconnection() throws IOException{
        ListeClients.remove(pseudo);
        System.out.println("\nLe client " + pseudo + " déconnecte.");
        diffusion("\nclient " + pseudo + " a quitté la conversation");
        inputs.close();
        outputs.close();
        client.close();
    }
}
