/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Observable;

/**
 *
 * @author Amaya
 */
public class Reciever extends Observable implements Runnable{
    private DatagramSocket socket;
    private static Reciever instance=null;
    
    private Reciever() {
        try{
            socket = new DatagramSocket(Config.MY_PORT);
        }catch(SocketException e){
        }
    }
    
    public static Reciever getInstance(){
        if(instance==null){
            instance = new Reciever();
            return instance;
        }
        else{
            return instance;
        }
    }
    
    @Override
    public void run(){
        this.addObserver(Node.getInstance(Config.MY_IP, Config.MY_PORT, Config.MY_NAME));
        while(true){
            byte[] incomingData = new byte[500];
            DatagramPacket dgp = new DatagramPacket(incomingData, incomingData.length);
            
            try {
                socket.receive(dgp);
                if(incomingData.length>0){
                
                String recievedString = new String(dgp.getData());                
                    notifyObservers(recievedString);
                }
                
                
            } catch (IOException ex) {
                Logger.getLogger(Reciever.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

  
}
