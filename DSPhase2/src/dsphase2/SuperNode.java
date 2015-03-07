/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author Amaya
 */
public class SuperNode extends Node{

    private ArrayList<Node> children;
    
    SuperNode(String ip,int port){
        super(ip,port);
        children = new ArrayList<>();
    }
    
    
    @Override
    public void register(){
        String sentence;
        String modifiedSentence;
        
        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
        Socket clientSocket = null ;
        DataOutputStream outToServer ;
        BufferedReader inFromServer;
        try{
            clientSocket= new Socket(this.getIp(), this.getPort());
            
            outToServer=new DataOutputStream(clientSocket.getOutputStream());
            
            inFromServer= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            sentence = inFromUser.readLine();
            
            outToServer.writeBytes(sentence);
            
            modifiedSentence = inFromServer.readLine();
            System.out.println("FROM SERVER: " + modifiedSentence);
            
        }catch (UnknownHostException e){
            System.out.println(e.getMessage());
        }
        catch (EOFException e){
            System.out.println(e.getMessage()); 
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
        finally {
              if(clientSocket!=null){
                  try {
                      clientSocket.close();
                  }
                  catch (IOException e) {
                      System.out.println(e.getMessage());
                  }
              }
        }
    }
    
}
