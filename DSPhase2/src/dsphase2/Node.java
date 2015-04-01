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
public class Node extends Thread{
    private final String ip;
    private final int port;
    private final String name;
    private static Node instance=null;
    private boolean superNode;
  //  private final String bsIp;
   // private final int bsPort;
    private Communicator com;
    
    public static Node getInstance(String ip,int port, String name, String bsIp, int bsPort){
        if(instance==null){
            instance = new Node(ip,port,name,bsIp,bsPort);
        }
        return instance;
    }
    
    private Node(String ip,int port,String name,String bsIp, int bsPort){
        this.ip=ip;
        this.port=port; 
        this.name=name;
        //this.bsIp=bsIp;
        //this.bsPort=bsPort;
        com = new Communicator(bsIp, bsPort);
    }
    
    public String getIp(){
        return ip;
    }
    
    public int getPort(){
        return port;
    }
    
    /*
        Register node in super node
    */
    private void register(){
        
        String message=(new Message(MessageType.REG,ip,port,name)).getMessage();

       // String response = sendTCPMessage(message); 
        String response = com.sendTCPMessage(message);
        
        System.out.println("Response:"+response);
        String[] splitted = response.split(" ");

        String noOfNodes=splitted[2];
        String[] peerIps ;
        int[] peerPorts ;

        System.out.println(noOfNodes);

        switch(noOfNodes.trim()){
            case "0":superNode=true;
                    break;
            case "1":superNode=true;
                    peerIps = new String[1];
                    peerPorts = new int[1];
                    peerIps[0]=splitted[3];
                    peerPorts[0]=Integer.parseInt(splitted[4]);
                    System.out.println(joinNetwork(peerIps[0], peerPorts[0]));
                    break;
            case "9996":System.out.println("Failed, can’t register. BS full.");
                        break;
            case "9997":System.out.println("Failed, registered to another user, try a different IP and port");
                        break;
            case "9998":System.out.println("Failed, already registered to you, unregister first");
                        break;
            case "9999":System.out.println("Failed, there is some error in the command");
                        break;
                    
            default:if(isSuper()){
                        superNode=true;
                    }
                    int number = Integer.parseInt(noOfNodes);
                    peerIps=new String[number];
                    peerPorts=new int[number];
                    System.out.println("number:"+number);
                    for(int i=1;i<number+1;i++){
                        peerIps[i-1]=splitted[3*i];
                        peerPorts[i-1]=Integer.parseInt(splitted[3*i+1]);
                        System.out.println(peerIps[i-1]+","+peerPorts[i-1]);
                    } 
                    for(int i=0;i<2;i++){
                        int[] array= getRandomTwo(number);
                        System.out.println(joinNetwork(peerIps[array[i]], peerPorts[array[i]]));
                    }
                
        }
    }
    
    private void unregister(){
        String message =(new Message(MessageType.UNREG,ip,port,name)).getMessage();

        //sendTCPMessage(message);
        com.sendTCPMessage(message);
    }
    
    private String joinNetwork( String peerIp, int peerPort){
        String message=(new Message(MessageType.JOIN,ip,port,name)).getMessage();
        
        //String response =  sendUDPMessage(message, peerIp, peerPort);
        String response = com.sendUDPMessage(message, peerIp, peerPort);
        
        return response;
    }
    
    private boolean isSuper(){
        if(Math.random()>=0.5){
            return true;
        }
        else{
            return false;
        }
    }
    
    private int[] getRandomTwo(int number){
        int rand1 = (int) (Math.random()*1000%number);
        int rand2 = (int) (Math.random()*1000%(number));
        
        while(rand1==rand2){
            rand2=(int) ((Math.random()*1000)%(number));
        }
        int[] array = {rand1,rand2};
        return array;
        
    }    

    
    @Override
    public void run(){
       register();   

      //unregister();
    }
    

}
