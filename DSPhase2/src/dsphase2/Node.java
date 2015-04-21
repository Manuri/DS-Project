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

    private final String myIp;
    private final int myPort;
    private final String myName;
    private static Node instance = null;
    private boolean isSuper;
    // only available if this is a normal node
    private String mySuperNode;  // supernode = "peer_IP:port_no"
    //only available if this is a super node
    private ArrayList<String> superPeers = new ArrayList<>();
    private ArrayList<String> childNodes = new ArrayList<>();
    private int inquireResponses;
    //private final Sender com;

    private HashMap<String, Integer> joinRequestSentPeers;

    public static Node getInstance(String ip, int port, String name) {
        if (instance == null) {
            instance = new Node(ip, port, name);
        }
        return instance;
    }

    private Node(String ip, int port, String name) {
        this.myIp = ip;
        this.myPort = port;
        this.myName = name;
        isSuper = Config.isSuper;
        joinRequestSentPeers = new HashMap<String, Integer>();
        addMyFiles(2
        );
        if (isSuper) {
            addChidrensFiles();
        }
        this.addObserver(Config.CONFIG_WINDOW);
    }

    private void addMyFiles(int numberOfNodes) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("src/resources/FileNames")));
            String readLine = null;
            int lineNumber = 0;
            String[] ipComponents = myIp.split("\\.");
            int ipRemainder = Integer.parseInt(ipComponents[3]) % numberOfNodes;
            while ((readLine = reader.readLine()) != null) {
                if (lineNumber % numberOfNodes == ipRemainder) {
                    String[] terms = readLine.split(" ");
                    for (String term : terms) {
                        if (myFiles.containsKey(term)) {
                            (myFiles.get(term)).add(readLine.toLowerCase());
                        } else {
                            ArrayList<String> files = new ArrayList<String>();
                            files.add(readLine.toLowerCase());
                            myFiles.put(term, files);
                        }
                    }
                }
                lineNumber++;
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
        return myIp;
    }

    public int getPort() {
        return myPort;
    }

    /*
     Register node in super node
     */
    public RegisterResponse register() {

        String message = (new Message(MessageType.REG, myIp, myPort, myName)).getMessage();
        UpdateTheLog("<Sending msg> " + message + " >> to BS");
        String response = Sender.getInstance().sendTCPMessage(message);

        UpdateTheLog("<Received msg> " + response + " << from BS");
        System.out.println("Response:" + response);
        String[] splitted = response.split(" ");

        String noOfNodes = splitted[2];
        String[] peerIps;
        int[] peerPorts;

        System.out.println(noOfNodes);

        switch (noOfNodes.trim()) {
            case "0":
                isSuper = true;
                return new RegisterResponse(MessageType.REG_SUCCESS, null, null);
            // break;
            case "1":
                isSuper = true;
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
        String message = (new Message(MessageType.UNREG, myIp, myPort, myName)).getMessage();

        Sender.getInstance().sendTCPMessage(message);
    }

    //can pass another argument(eg: String arg) and pass that on to Message constructor 
    //in addition to what is already in it. Then we can replace the function below with this 
//    private void sendMessage(MessageType msgType, String peerIp, int peerPort) {
//        String message = (new Message(msgType, myIp, myPort, name)).getMessage();
//        UpdateTheLog("<Sending msg> "+message);
//        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
//    }
    private void sendMessage(String message, String peerIp, int peerPort) {
        System.out.println("sending message: " + message + " from:" + Config.MY_IP + ":" + Config.MY_PORT + " to:" + peerIp + ":" + peerPort);
        Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
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

    public HashMap getMyFiles() {
        return myFiles;
    }

    @Override
    public void update(Observable o, Object arg) {
        //Process incoming message
        UDPResponse receivedMessage = (UDPResponse) arg;
        String incoming = receivedMessage.getData();
        System.out.println("incoming message:" + incoming);
        String[] msg = incoming.split(" ");
        MessageType msgType = MessageType.valueOf(msg[1]);
        String requesterIp = null;
        int requesterPort = 0;

        
        /* if (msgType == MessageType.SEROK) {
         peerIp = msg[3];
         peerPort = Integer.parseInt(msg[4]);
         } else if(msgType == MessageType.JOINOK){}
         else {
         peerIp = msg[2];
         System.out.println("Peer port: "+msg[3]);
         peerPort = Integer.parseInt(msg[3].trim());
            
         }*/
        switch (msgType) {
            case SEROK:
                requesterIp = msg[3].trim();
                requesterPort = Integer.parseInt(msg[4].trim());
                break;

            case JOINOK:
                break;

            default:
                requesterIp = msg[2].trim();
                System.out.println("Peer port: " + msg[3]);
                requesterPort = Integer.parseInt(msg[3].trim());
        }
        String info = requesterIp + ":" + requesterPort;
        String outGoingMessage;
        switch (msgType) {
            // for inquire msg : <length INQUIRE IP_address port_no is_super>
            case INQUIRE:
                System.out.println("Received INQUIRE message");
                if (isSuper) {
                    outGoingMessage = (new Message(MessageType.INQUIREOK, myIp, myPort, "")).getMessage();
                    sendMessage(outGoingMessage, requesterIp, requesterPort);
                } else {
                    String[] superNodeInfo = mySuperNode.split(":");
                    String message = (new Message(MessageType.INQUIREOK, superNodeInfo[0], Integer.parseInt(superNodeInfo[1]), "")).getMessage();
                    sendMessage(message, requesterIp, requesterPort);
                }
                break;
            // for inquire reply: <length INQUIREOK IP_address port_no> 
            case INQUIREOK:
                inquireResponses--;
                String name = myName;
                if (isSuper) {
                    name = "SUPER" + name;
                } else {
                    name = "NORMAL" + name;
                }
                outGoingMessage = (new Message(MessageType.JOIN, myIp, myPort, name)).getMessage();
                sendMessage(outGoingMessage, requesterIp, requesterPort);
                joinRequestSentPeers.put(requesterIp, requesterPort);
                break;
            // for join req : <length JOIN IP_address port_no>
            case JOIN:
                String requesterName = (msg[4]).substring(5);
                if ((msg[4]).startsWith("SUPER")) {
                    System.out.println("Added peer super node:" + requesterName);
                    superPeers.add(info);
                } else if ((msg[4]).startsWith("NORMAL")) {
                    System.out.println("Added child node:" + requesterName);
                    childNodes.add(info);
                }
                outGoingMessage = (new Message(MessageType.JOINOK, myIp, myPort, myName)).getMessage();
                sendMessage(outGoingMessage, requesterIp, requesterPort);
                break;
            //for join resp length JOINOK value
            case JOINOK:
                String joinedPeerIp = receivedMessage.getIpAddress();
                int joinedPeerPort = joinRequestSentPeers.get(joinedPeerIp);
                joinRequestSentPeers.remove(joinedPeerIp);
                info = joinedPeerIp + ":" + joinedPeerPort;
                if (isSuper) {
                    String superPeer = info;
                    superPeers.add(superPeer);
                    System.out.println("Added peer super node: " + info);
                } else {
                    mySuperNode = info;
                    System.out.println("Added my super node: " + info);
                }
                break;
            case SER:
                String[] messageComponents = incoming.split("\"");
                String[] searcherIpPort = messageComponents[0].split(" ");
                String searcherIp = searcherIpPort[2];
                int searcherPort = Integer.parseInt(searcherIpPort[3]);
                String fileKey = messageComponents[1];
                int hopCount = 1;
                System.out.println("Hop count:" + messageComponents[2]);
                if (!(messageComponents[2].length()==0)) {
                    hopCount = 1 + Integer.parseInt(messageComponents[2].trim());
                }
                System.out.println("Search message received for key:" + fileKey);
                //check if I have the file
                if (hopCount < Config.TTL) {
                    if (myFiles.containsKey(fileKey.toLowerCase())) {
                        ArrayList<String> files = myFiles.get(fileKey);
                        int noOfFiles = files.size();
                        //first send the list of files to the searcher
                        String response = (new Message(MessageType.SEROK, noOfFiles, Config.MY_IP, Config.MY_PORT, hopCount, files)).getMessage();
                        System.out.println("Created response:" + response);
                        sendMessage(response, searcherIp, searcherPort);
                    }
                    //if I am a super peer, forward the search message to respective peers
                    if (isSuper) {
                        //forward the search query to a random peers
                        int randomPeerNumer = getRandomNo(superPeers.size());
                        String[] ipPort = (superPeers.get(randomPeerNumer)).split(":");
                        search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);

                        //next forward the search query to children having the file
                        if (chilrensFiles.containsKey(fileKey)) {
                            String[] peersWithFile = chilrensFiles.get(fileKey);
                            for (String peer : peersWithFile) {
                                ipPort = peer.split(":");
                                search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
                            }
                        }
                    }
//                    
//                    if (isSuper) {
//                        //check if my children have the file
//                        if (chilrensFiles.containsKey(fileKey)) {
//                            String[] peersWithFile = chilrensFiles.get(fileKey);
//                            for (String peer : peersWithFile) {
//                                String[] ipPort = peer.split(":");
//                                search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
//                            }
//                        } //else, search it from other connected super peers
//                        else {
//                            for (String peer : superPeers) {
//                                String[] ipPort = peer.split(":");
//                                search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
//                            }
//                        }
//                    }
                }
                break;
            case SEROK:
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
                int length = msg.length;
                //if its just a child asking to leave, remove him from the childNodes list 
                //and remove all file names from the super node which were in the leaving node but not in any other children
                if ("CHILD-LEAVING".equals(msg[length].trim())) {
                    //TO-DO: need to remove all file names from the super node which were in the leaving node but not in any other children
                    childNodes.remove(msg[2].trim() + ":" + msg[3].trim());
                } //if it is a super node that is leaving, take the ip and port it sends and send an INQUIRE message to it asking to connect
                else {
                    String[] ipPort = msg[length].split(":");
                    outGoingMessage = (new Message(MessageType.INQUIRE, myIp, myPort, "")).getMessage();
                    sendMessage(outGoingMessage, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
                }

            case LEAVEOK:
                break;

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
            if (peerIPs != null) {
                for (String i : peerIPs) {
                    System.out.println(i);
                }
            }
            int[] peerPorts = response.getpeerPorts();
            if (peerIPs != null) {
                if (isSuper) {
                    System.out.println("I am Super");
                    int[] randomPeers;
                    if (peerIPs.length >= 2) {
                        //get random 2 peers to connect and check for super peer
                        randomPeers = getRandomTwo(peerIPs.length);
                        inquireResponses = 2;
                        for (int peer : randomPeers) {
                            System.out.println("random peer: " + peerIPs[peer]);
                            String outGoingMessage = (new Message(MessageType.INQUIRE, myIp, myPort, "")).getMessage();
                            sendMessage(outGoingMessage, peerIPs[peer], peerPorts[peer]);
                        }
                    } else {
                        if (peerIPs.length == 1) {
                            String outGoingMessage = (new Message(MessageType.INQUIRE, myIp, myPort, "")).getMessage();
                            sendMessage(outGoingMessage, peerIPs[0], peerPorts[0]);
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
                        String outGoingMessage = (new Message(MessageType.INQUIRE, myIp, myPort, "")).getMessage();
                        sendMessage(outGoingMessage, peerIPs[peer], peerPorts[peer]);
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
        String message = "Peer " + myName + " joined the network...";
        System.out.println(message);
        //UpdateTheLog(message);
    }

    public void search(String fileName) {
        String[] ipPort;
        if (isSuper) {
            int noOfSuperPeers = superPeers.size();
            int randomSuperPeer = getRandomNo(noOfSuperPeers);
            System.out.println("Selected super peer to send search message: " + superPeers.get(randomSuperPeer));
            ipPort = (superPeers.get(randomSuperPeer)).split(":");
        } else {
            ipPort = mySuperNode.split(":");
        }
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
        search(fileName, myIp, myPort, peerIp, peerPort);
    }

    public void UpdateTheLog(String msg) {
        setChanged();
        notifyObservers(msg);
        clearChanged();
    }

    public void leave() {
        String[] ipPort;
        String message;
        //if I am a superNode 
        if (isSuper) {
            //send messages to all peers saying I am leaving and give them the ip and port of another super peer to connect with
            int noOfPeers = superPeers.size();
            for (int i = 0; i < noOfPeers; i++) {
                ipPort = superPeers.get(i).split(":");
                if (i < noOfPeers / 2) {
                    //if the index of the super peer in the super peer array list is < length/2 then direct him to join (i+1)th super peer
                    message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i + 1))).getMessage();
                } else {
                    message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i - 1))).getMessage();
                }
                sendMessage(message, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
            }

            //send messages to all children saying I am leaving and give them the ip and port of a super peer to connect with
            int noOfChildren = childNodes.size();
            for (int i = 0; i < noOfChildren; i++) {
                ipPort = childNodes.get(i).split(":");
                if (i <= noOfPeers) {
                    message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i))).getMessage();
                } else {
                    message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i - noOfPeers))).getMessage();
                }
                sendMessage(message, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
            }
        } //if I am not a super Node then just tell the super peer that I am leaving
        else {
            ipPort = mySuperNode.split(":");
            //just pass null to show that I am a normal node
            message = (new Message(MessageType.LEAVE, myIp, myPort, null)).getMessage();
            sendMessage(message, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
        }
    }

}
