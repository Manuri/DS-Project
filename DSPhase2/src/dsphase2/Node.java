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
    
    public static Node getInstance(String ip,int port, String name){
        if(instance==null){
          //  instance = new Node("127.0.0.1",5000,"abcd");
            instance = new Node(ip,port,name);
        }
        return instance;
    }
    
    private Node(String ip,int port,String name){
        this.ip=ip;
        this.port=port; 
        this.name=name;
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
    private void register(String myIp, int myPort, String myName, String bootstrapIp, int booStrapPort){
        
        String message = "REG"+" "+myIp+" "+myPort+" "+myName;

        message = appendLength(message);
        String response = sendTCPMessage(message,bootstrapIp,booStrapPort);
        
        //String response = sendTCPMessage("0036 REG 129.82.123.45 5001 1234abcd",bootstrapIp,booStrapPort);
        
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
                    System.out.println(joinNetwork(ip, port, peerIps[0], peerPorts[0]));
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
                        //System.out.println(joinNetwork(myIp, myPort, peerIps[i-1], peerPorts[i-1]));
                    } 
                    for(int i=0;i<2;i++){
                        int[] array= getRandomTwo(number);
                        System.out.println(joinNetwork(myIp, myPort, peerIps[array[i]], peerPorts[array[i]]));
                    }
                
        }
    }
    
    private void unregister(String myIp, int myPort, String myName, String bootstrapIp, int booStrapPort){
        String message = "UNREG"+" "+myIp+" "+myPort+" "+myName;
        
        message=appendLength(message);
        
       // System.out.println(message);
        sendTCPMessage(message, bootstrapIp, booStrapPort);
       // sendTCPMessage("0038 UNREG 129.82.123.45 5001 1234abcd", bootstrapIp, booStrapPort);
    }
    
    private String joinNetwork(String myIp, int myPort, String peerIp, int peerPort){
        String message="JOIN"+" "+myIp+" "+myPort;
        message=appendLength(message);
        String response =  sendUDPMessage(message, peerIp, peerPort);
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
    
    private String appendLength(String message){
         int messageLength = message.length()+4+1;
        String messageLengthString = Integer.toString(messageLength);
        String prefix="";
        switch(messageLengthString.length()){
            case 1: prefix="000"+messageLengthString+" ";
                break;
            case 2:prefix = "00"+messageLengthString+" ";
                break;
            case 3:prefix="0"+messageLengthString+" ";
                break;
            case 4: prefix=messageLengthString+" ";
                break;
        }
        message=prefix+message;
        
        return message;
    }
    
    private String sendUDPMessage(String message, String ip, int port){
        try{
            DatagramSocket clientSocket = new DatagramSocket(); 
            InetAddress IPAddress = InetAddress.getByName(ip); 
            
            byte[] toSend  = message.getBytes(); 
		  
            DatagramPacket packet =new DatagramPacket(toSend, toSend.length, IPAddress, port); 
		    
            clientSocket.send(packet); 
            }
        catch(IOException ioe){
            System.out.println(ioe.getMessage());
	}
        return null;
    }
    
    private String sendTCPMessage(String sentence, String ip, int port){
        System.out.println("inside send message"+sentence);
        
        Socket clientSocket = null;
        PrintWriter outToServer=null;
        BufferedReader inFromServer=null;
        char[] buf=null;
        try{
            clientSocket = new Socket(ip, port);
            
             outToServer = new PrintWriter(clientSocket.getOutputStream(),true);
  
             inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
          
            outToServer.println(sentence);  
             //outToServer.println("0036 REG 129.82.123.45 5001 1234abcd");  

           buf=new char[1000];

            inFromServer.read(buf);
            System.out.println(buf); 
        }
        catch(UnknownHostException e){
            System.out.println("aaa");
            System.out.println(e.getMessage());
        } 
        catch (IOException ex) {
            System.out.println("bbb");
            System.out.println(ex.getMessage());
            System.out.println("ddd");
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
                System.out.println("ccc");
                System.out.println(ex.getMessage());
            }
        }
        
        return String.valueOf(buf);
       // return output;
    }
    
    @Override
    public void run(){
       register(ip,port,name,"localhost",9876);   

      //unregister(ip, port, name, "localhost",9876);
    }
    

}
