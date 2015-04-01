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
import java.net.UnknownHostException;

/**
 *
 * @author Amaya
 */
public class Communicator {
    private final String bootstrapIP;
    private final int bootStrapPort;
    
    public Communicator(String ip, int port){
        bootstrapIP=ip;
        bootStrapPort=port;
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
            System.out.println(ioe.getMessage());
	}
        return null;
    }
    
    public String sendTCPMessage(String sentence){
        System.out.println("inside send message"+sentence);
        
        Socket clientSocket = null;
        PrintWriter outToServer=null;
        BufferedReader inFromServer=null;
        char[] buf=null;
        try{
            clientSocket = new Socket(bootstrapIP, bootStrapPort);
            
             outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
  
             inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          
            outToServer.println(sentence);   

           buf=new char[1000];

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
        
        return String.valueOf(buf);
    }
    
}
