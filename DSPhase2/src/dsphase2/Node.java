/*
 * CS4262 Distributed Systems Mini Project
 */
package dsphase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amaya
 */
public class Node extends Observable implements Observer {

    private final String ip;
    private final int port;
    private final String name;
    private static Node instance = null;
    private boolean superNode;
    // only available if this is a normal node
    private String supernode;  // supernode = "peer_IP:port_no"
    //only available if this is a super node
    private ArrayList<String> superPeers = new ArrayList<>();
    private ArrayList<String> childNodes = new ArrayList<>();
    private int inquireResponses;
    //private final Sender com;

    public static Node getInstance(String ip, int port, String name) {
        if (instance == null) {
            instance = new Node(ip, port, name);
        }
        return instance;
    }

    private Node(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        superNode = Config.isSuper;
        addMyFiles();
        if (superNode) {
            addChidrensFiles();
        }
        this.addObserver(Config.CONFIG_WINDOW);
    }

    private void addMyFiles() {
        
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("src/resources/FileNames")));
            String readLine = null;
            int lineNumber = 0;
            String[] ipComponents = ip.split("\\.");
            int ipRemainder = Integer.parseInt(ipComponents[3]) % 3;
            while((readLine = reader.readLine()) != null){
                if (lineNumber % 3 == ipRemainder){
                    String[] terms = readLine.split(" ");
                    for (String term : terms){
                        if (myFiles.containsKey(term)){
                            (myFiles.get(term)).add(readLine.toLowerCase());
                        }
                        else{
                            ArrayList<String> files = new ArrayList<String>();
                            files.add(readLine.toLowerCase());
                            myFiles.put(term,files);
                        }
                    }
                }
                lineNumber ++;
            //myFiles.put("Adventures",new String[]{"Adventured of Tintin"});
            //myFiles.put("Harry", new String[]{"Harry Potter"});
            
            }
            //myFiles.put("Windows",new String[]{"Windows XP","Windows 8"});
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            System.out.println(myFiles);
        }
    }

    private void addChidrensFiles() {
        chilrensFiles.put("Adventures", new String[]{"127.0.0.1:5001"});
        chilrensFiles.put("Windows", new String[]{"127.0.0.1:5001", "127.0.0.3:5001"});
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    /*
     Register node in super node
     */
    public RegisterResponse register() {

        String message = (new Message(MessageType.REG, ip, port, name)).getMessage();
        UpdateTheLog("<Sending msg> "+message + " >> to BS"); 
        String response = Sender.getInstance().sendTCPMessage(message);
        UpdateTheLog("<Received msg> "+response+" << from BS");
        System.out.println("Response:" + response);
        String[] splitted = response.split(" ");

        String noOfNodes = splitted[2];
        String[] peerIps;
        int[] peerPorts;

        System.out.println(noOfNodes);

        switch (noOfNodes.trim()) {
            case "0":
                superNode = true;
                return new RegisterResponse(MessageType.REG_SUCCESS, null, null);
            // break;
            case "1":
                superNode = true;
                peerIps = new String[1];
                peerPorts = new int[1];
                peerIps[0] = splitted[3];
                peerPorts[0] = Integer.parseInt(splitted[4]);
                //  System.out.println(joinNetwork(peerIps[0], peerPorts[0]));
                return new RegisterResponse(MessageType.REG_SUCCESS, peerIps, peerPorts);
            //  break;
            case "9996":
                System.out.println("Failed, can’t register. BS full.");
                UpdateTheLog("Failed, can’t register. BS full."); 
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
            //     break;
            case "9997":
                System.out.println("Failed, registered to another user, try a different IP and port");
                UpdateTheLog("Failed, registered to another user, try a different IP and port");
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
            //  break;
            case "9998":
                System.out.println("Failed, already registered to you, unregister first");
                UpdateTheLog("Failed, already registered to you, unregister first");
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
            // break;
            case "9999":
                System.out.println("Failed, there is some error in the command");
                UpdateTheLog("Failed, there is some error in the command");
                return new RegisterResponse(MessageType.REG_FAILURE, null, null);
            //  break;

            default:
                if (setSuper()) {
                    superNode = true;
                }
                int number = Integer.parseInt(noOfNodes);
                peerIps = new String[number];
                peerPorts = new int[number];
                System.out.println("number:" + number);
                for (int i = 1; i < number + 1; i++) {
                    peerIps[i - 1] = splitted[3 * i];
                    peerPorts[i - 1] = Integer.parseInt(splitted[3 * i + 1]);
                    System.out.println(peerIps[i - 1] + "," + peerPorts[i - 1]);
                }

                return new RegisterResponse(MessageType.REG_SUCCESS, peerIps, peerPorts);

        }
    }

    private void unregister() {
        String message = (new Message(MessageType.UNREG, ip, port, name)).getMessage();

        Sender.getInstance().sendTCPMessage(message);
    }

    private void sendMessage(MessageType msgType, String peerIp, int peerPort) {
        String message = (new Message(msgType, ip, port, name)).getMessage();
        UpdateTheLog("<Sending msg> "+message);
        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
    }

    private void sendMessage(String message, String peerIp, int peerPort) {
        System.out.println("sending message: " + message + " from:" + Config.MY_IP + ":" + Config.MY_PORT + " to:" + peerIp + ":" + peerPort);
        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
    }

    private boolean setSuper() {
        if (Math.random() >= 0.5) {
            return true;
        } else {
            return false;
        }
    }

    private int[] getRandomTwo(int number) {

        if (number == 2) {
            return new int[]{0, 1};
        }
        int rand1 = (int) (Math.random() * 1000 % number);
        int rand2 = (int) (Math.random() * 1000 % (number));

        while (rand1 == rand2) {
            rand2 = (int) ((Math.random() * 1000) % (number));
        }
        int[] array = {rand1, rand2};
        return array;
    }

    private int getRandomNo(int number) {
        return (int) (Math.random() * 1000 % number);
    }
    //my files
    //stored as an invereted index
    //format term:set of files
    private HashMap<String, ArrayList<String>> myFiles = new HashMap<String, ArrayList<String>>();
    //Store the files children have in key,peers format
    private HashMap<String, String[]> chilrensFiles = new HashMap<String, String[]>();

    public HashMap getMyFiles(){
        return myFiles;
    }
    
    @Override
    public void update(Observable o, Object arg) {
        //Process incoming message
        String incoming = (String) arg;
        System.out.println("incoming message:"+incoming);
        String[] msg = incoming.split(" ");
        MessageType msgType = MessageType.valueOf(msg[1]);
        String peerIp = null;
        int peerPort = 0;
       /* if (msgType == MessageType.SEROK) {
            peerIp = msg[3];
            peerPort = Integer.parseInt(msg[4]);
        } else if(msgType == MessageType.JOINOK){}
        else {
            peerIp = msg[2];
            System.out.println("Peer port: "+msg[3]);
            peerPort = Integer.parseInt(msg[3].trim());
            
        }*/
        switch(msgType){
            case SEROK:            
                peerIp = msg[3].trim();
                peerPort = Integer.parseInt(msg[4].trim());
                break;
                
            case LEAVE:
                
                
            case LEAVEOK:
                break;
                
            default: 
                peerIp = msg[2].trim();
                System.out.println("Peer port: "+msg[3]);
                peerPort = Integer.parseInt(msg[3].trim());                
        }
        
        switch (msgType) {
            // for inquire msg : <length INQUIRE IP_address port_no is_super>
            case INQUIRE:
                System.out.println("Received INQUIRE message");
                if (superNode) {
                    sendMessage(MessageType.INQUIREOK, ip, port);
                } else {
                    String[] superNodeInfo = supernode.split(":");
                    sendMessage(MessageType.INQUIREOK, superNodeInfo[0], Integer.parseInt(superNodeInfo[1]));
                }
                break;
            // for inquire reply: <length INQUIREOK IP_address port_no> 
            case INQUIREOK:
                inquireResponses--;
                sendMessage(MessageType.JOIN, peerIp, peerPort);
                String info = peerIp + ":" + peerPort;
                if (superNode) {
                    String superPeer = info;
                    superPeers.add(superPeer);
                } else {
                    supernode = info;
                }
                break;
            // for join req : <length JOIN IP_address port_no>
            case JOIN:
                childNodes.add(peerIp + ":" + peerPort);
                sendMessage(MessageType.JOINOK, ip, port);
                break;
            //for join resp length JOINOK value
            case JOINOK:
                break;
            case SER:
                incoming = (String) arg;

                String[] messageComponents = incoming.split("\"");
                String[] searcherIpPort = messageComponents[0].split(" ");
                String searcherIp = searcherIpPort[2];
                int searcherPort = Integer.parseInt(searcherIpPort[3]);
                String fileKey = messageComponents[1];
                int hopCount = 1;
                if ("\"".equals(messageComponents[2])) {
                    hopCount = 1 + Integer.parseInt(messageComponents[2].substring(1));
                }
                System.out.println("Search message received for key:" + fileKey);
                //check if I have the file
                if (myFiles.containsKey(fileKey.toLowerCase())) {
                    ArrayList<String> files = myFiles.get(fileKey);
                    int noOfFiles = files.size();
                    String response = (new Message(MessageType.SEROK, noOfFiles, Config.MY_IP, Config.MY_PORT, hopCount, files)).getMessage();
                    System.out.println("Created response:" + response);
                    sendMessage(response, searcherIp, searcherPort);
                }
                if (superNode) {
                    //check if my children have the file
                    if (chilrensFiles.containsKey(fileKey)) {
                        String[] peersWithFile = chilrensFiles.get(fileKey);
                        for (String peer : peersWithFile) {
                            String[] ipPort = peer.split(":");
                            search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
                        }
                    } //else, search it from other connected super peers
                    else {
                        for (String peer : superPeers) {
                            String[] ipPort = peer.split(":");
                            search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
                        }
                    }
                }
                break;
            case SEROK:
                incoming = (String) arg;
                String[] parts = incoming.split(" ");
                int noOfFiles = Integer.parseInt(parts[2]);
                switch (noOfFiles) {
                    case 0:
                        System.out.println("Files not found!");
                        break;
                    case 1:
                        System.out.println("Files found:");
                        System.out.println(incoming);
                        break;
                    case 9999:
                        System.out.println("Node unreachable");
                        break;
                    case 9998:
                        System.out.println("Unknown error occured...");
                        break;

                }
                break;
            case LEAVE:
                incoming = (String) arg;
        }

    }

    public void start() {

        RegisterResponse response = register();

        if (response.isSucess()) {
           
            Thread reciever = new Thread(Reciever.getInstance());
            reciever.start();

            //now join the network
            String[] peerIPs = response.getPeerIps();
            System.out.println("Peer IPs");
            if (peerIPs!=null) {               
                for (String i : peerIPs) {
                    System.out.println(i);
                }
            }
            int[] peerPorts = response.getpeerPorts();
            if (peerIPs != null) {
                if (superNode) {
                    System.out.println("I am Super");
                    int[] arr;
                    if (peerIPs.length >= 2) {
                        //get random 2 peers to connect and check for super peer
                        arr = getRandomTwo(peerIPs.length);
                        inquireResponses = 2;
                        for (int peer : arr) {
                            System.out.println("random peer: " + peerIPs[peer]);
                            sendMessage(MessageType.INQUIRE, peerIPs[peer], peerPorts[peer]);
                        }
                    } else {
                        if (peerIPs.length == 1) {
                            sendMessage(MessageType.INQUIRE, peerIPs[0], peerPorts[0]);
                        }
                    }
                } else {
                    System.out.println("I am Normal");
                    // get a peer to connect and check for super peer
                    if (peerIPs.length > 0) {
                        int peer;
                        if (peerIPs.length == 1) {
                            peer = 0;
                        } else {
                            peer = getRandomNo(peerIPs.length);
                        }
                        inquireResponses = 1;
                        System.out.println("random peer: " + peerIPs[peer]);
                        sendMessage(MessageType.INQUIRE, peerIPs[peer], peerPorts[peer]);
                    }
                }
            }
//            // wait until all responses are received for INQUIRE message
//            while (inquireResponses != 0) {
//                continue;
//            }
//            
//            while(true){
//                continue;
//            }
        }
    }

    public void search(String fileName) {
        String[] ipPort = supernode.split(":");
        search(fileName, ipPort[0], Integer.parseInt(ipPort[1]));
    }

    public void search(String fileName, String peerIp, int peerPort, int hopCount) {
        search(fileName, Config.MY_IP, Config.MY_PORT, peerIp, peerPort, hopCount);
    }

    public void search(String fileName, String searcherIp, int searcherPort, String peerIp, int peerPort, int hopCount) {
        String fileNameString = "\"" + fileName + "\"";
        String message = (new Message(MessageType.SER, searcherIp, searcherPort, fileNameString, hopCount)).getMessage();
        System.out.println("created message" + message);
        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        System.out.println("Message sent:" + message);
    }

    public void search(String fileName, String searcherIp, int searcherPort, String peerIp, int peerPort) {
        String fileNameString = "\"" + fileName + "\"";
        String message = (new Message(MessageType.SER, searcherIp, searcherPort, fileNameString)).getMessage();
        System.out.println("created message" + message);
        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        System.out.println("Message Sent:" + message);
    }

    public void search(String fileName, String peerIp, int peerPort) {
        search(fileName, Config.MY_IP, Config.MY_PORT, peerIp, peerPort);
    }
    
    
    public void UpdateTheLog(String msg){
        setChanged();
        notifyObservers(msg);
        clearChanged();
    }
    
    public void leave(){
        String[] ipPort;
        if(superNode){
            for(String peer : superPeers){
                ipPort= peer.split(":");
                sendMessage(MessageType.LEAVE,ipPort[0].trim() , Integer.parseInt(ipPort[1].trim()));
            }
            
            for(String child: childNodes){
                ipPort= child.split(":");
                sendMessage(MessageType.LEAVE,ipPort[0].trim() , Integer.parseInt(ipPort[1].trim()));                
            }
        }
        else{
            ipPort=supernode.split(":");
            sendMessage(MessageType.LEAVE, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
        }
    }
        
}
