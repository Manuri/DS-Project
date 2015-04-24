/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Amaya
 */
public class Sender {
    private final String bootstrapIP;
    private final int bootStrapPort;
    private static Sender instance=null;
    
    private Sender(){
        bootstrapIP=Config.BOOTSTRAP_IP;
        bootStrapPort=Config.BOOTSTRAP_PORT;
    }
    
    public static Sender getInstance(){
        if(instance==null){
            instance = new Sender();
            return instance;
        }
        else{
            return instance;
        }
    }
    
    public String sendUDPMessage(String message, String peerIp, int peerPort){
        try{
            DatagramSocket clientSocket = new DatagramSocket(); 
            InetAddress IPAddress = InetAddress.getByName(peerIp); 
            
            byte[] toSend  = message.getBytes(); 
		  
            DatagramPacket packet =new DatagramPacket(toSend, toSend.length, IPAddress, peerPort); 
		    
            clientSocket.send(packet); 
            }
        catch(IOException ioe){
            ioe.printStackTrace();
	}
        return null;
    }
    
    public String sendTCPMessage(String sentence){
        System.out.println("inside send message"+sentence);
        
        Socket clientSocket = null;
        PrintWriter outToServer=null;
        BufferedReader inFromServer=null;
        char[] buf=new char[10000];
        try{
            clientSocket = new Socket(bootstrapIP, bootStrapPort);
            
             outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
  
             inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          
            outToServer.println(sentence);   

           buf=new char[10000];

            inFromServer.read(buf);
            System.out.println(buf); 
        }
        catch(UnknownHostException e){
            System.out.println(e.getMessage());
        } 
        catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        finally{
            try {
                if(outToServer!=null)
                    outToServer.close();
                
                if(inFromServer!=null)
                    inFromServer.close();
                
                if(clientSocket!=null)
                    clientSocket.close();
                
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        return String.valueOf(buf).trim();
    }
    
}
