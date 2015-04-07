/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/**
 *
 * @author Amaya
 */
public class Node implements Observer{
    private final String ip;
    private final int port;
    private final String name;
    private static Node instance=null;
    private boolean superNode;
    //private final Sender com;
    
    public static Node getInstance(String ip,int port, String name){
        if(instance==null){
            instance = new Node(ip,port,name);
        }
        return instance;
    }
    
    private Node(String ip,int port,String name){
        this.ip=ip;
        this.port=port; 
        this.name=name;
        myFiles.put("Adventures",new String[]{"Adventured of Tintin"});
        myFiles.put("Harry",new String[]{"Harry Potter"});
        myFiles.put("Windows",new String[]{"Windows XP","Windows 8"});
    
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
    public RegisterResponse register(){
        
        String message=(new Message(MessageType.REG,ip,port,name)).getMessage();

        String response = Sender.getInstance().sendTCPMessage(message);
        
        System.out.println("Response:"+response);
        String[] splitted = response.split(" ");

        String noOfNodes=splitted[2];
        String[] peerIps ;
        int[] peerPorts ;

        System.out.println(noOfNodes);

        switch(noOfNodes.trim()){
            case "0":superNode=true;
                    return new RegisterResponse(MessageType.REG_SUCCESS, null, null);
                   // break;
            case "1":superNode=true;
                    peerIps = new String[1];
                    peerPorts = new int[1];
                    peerIps[0]=splitted[3];
                    peerPorts[0]=Integer.parseInt(splitted[4]);
                  //  System.out.println(joinNetwork(peerIps[0], peerPorts[0]));
                    return new RegisterResponse(MessageType.REG_SUCCESS, peerIps, peerPorts);
                  //  break;
            case "9996":System.out.println("Failed, can’t register. BS full.");
                    return new RegisterResponse(MessageType.REG_FAILURE, null, null);
                   //     break;
            case "9997":System.out.println("Failed, registered to another user, try a different IP and port");
                    return new RegisterResponse(MessageType.REG_FAILURE, null, null);
                      //  break;
            case "9998":System.out.println("Failed, already registered to you, unregister first");
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
                       // break;
            case "9999":System.out.println("Failed, there is some error in the command");
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
                      //  break;
                    
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
                   /* for(int i=0;i<2;i++){
                        int[] array= getRandomTwo(number);
                        System.out.println(joinNetwork(peerIps[array[i]], peerPorts[array[i]]));
                    }*/
                    return new RegisterResponse(MessageType.REG_SUCCESS, peerIps, peerPorts);
                
        }
    }
    
    private void unregister(){
        String message =(new Message(MessageType.UNREG,ip,port,name)).getMessage();

        Sender.getInstance().sendTCPMessage(message);
    }
    
    private String joinNetwork( String peerIp, int peerPort){
        String message=(new Message(MessageType.JOIN,ip,port,name)).getMessage();
        
        String response = Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        
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

    //To be changed
    HashMap<String,String[]> myFiles = new HashMap<String, String[]>();
    
    
    @Override
    public void update(Observable o, Object arg) {
        String incoming = (String)arg;
        if (isSuper()){
            String[] messageComponents = incoming.split("\"");
            String fileName = messageComponents[1];
            
            
            //check if I have the file
            
            //check if the file is there in the childrenset including myself
            
        }
        //System.out.println(incoming);
        //Process incoming message
    }
    
    public void start(){
        
        //RegisterResponse response = register(); 
        
        //if(response.isSucess()){
            Thread reciever= new Thread(Reciever.getInstance());
            reciever.start();
            
            //now join the network
        //}
    }

    public void search(String fileName){
        String peerIp = "127.0.0.1";
        int peerPort = 5001;
        String fileNameString = "\""+fileName+"\"";
        String message = (new Message(MessageType.SER, peerIp, peerPort, fileNameString)).getMessage();
        String response = Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
                System.out.println("Returned from node:"+ response);
        //return response;
    }
    
    public void search(String fileName, int maxHops){
        String peerIp = "127.0.0.1";
        int peerPort = 5001;
        String message = (new Message(MessageType.SER, "127.0.0.1", 5001, fileName, maxHops)).getMessage();
        String response = Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        System.out.println("Returned from node:"+ response);
        //return response;
    }

    

}
