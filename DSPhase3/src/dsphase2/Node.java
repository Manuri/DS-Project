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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
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
    //private HashMap<String, String> routingTable = new HashMap<>();
    private HashSet<String> innerSet = new HashSet<>();
    private HashMap<String, HashSet<String>> routingTable = new HashMap<>();
    private int inquireResponses;
    //private final Sender com;

   // private HashMap<String, Integer> joinRequestSentPeers;

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
       // joinRequestSentPeers = new HashMap<>();
        //addMyFiles(4);
        this.addObserver(Config.CONFIG_WINDOW);
    }

    private void addMyFiles(int numberOfNodes) {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File("src/resources/FileNames")));
            String readLine = null;
            int lineNumber = 0;
            int ipRemainder = Config.myNodeNumber;
            UpdateTheLog("Adding my files");
            while ((readLine = reader.readLine()) != null) {
                if (lineNumber % numberOfNodes == ipRemainder) {
                    String[] terms = readLine.toLowerCase().split(" ");
                    String fileName = readLine.replace(" ", "_");
                    UpdateTheLog(fileName);
                    String previousTerm;
                    int count = 0;
                    int fileNameSize = terms.length;
                    if (fileNameSize > 2) {
                        addTermFile(readLine.toLowerCase(), fileName);
                    }
                    for (String term : terms) {
                        addTermFile(term, fileName);
                        if (count > 0) {
                            addTermFile(terms[count - 1] + " " + term, fileName);
                        }
                        count++;
                    }
                }
                lineNumber++;
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

    private void addTermFile(String term, String fileName) {
        if (myFiles.containsKey(term)) {
            (myFiles.get(term)).add(fileName);
        } else {
            ArrayList<String> files = new ArrayList<>();
            files.add(fileName);
            myFiles.put(term, files);
        }
    }

    private void addChildrensFiles(String termsString, String childIp, int childPort) {
        Set<String> childTerms = chilrensFiles.keySet();
        String[] terms = termsString.split(",");
        for (String term : terms) {
            if (childTerms.contains(term)) {
                ArrayList<String> childrenHavingTerm = chilrensFiles.get(term);
                childrenHavingTerm.add(childIp + ":" + String.valueOf(childPort));
            } else {
                ArrayList<String> ipAddress = new ArrayList<>();
                ipAddress.add(childIp + ":" + String.valueOf(childPort));
                chilrensFiles.put(term, ipAddress);
            }
        }
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
        Config.myNodeNumber = Integer.parseInt(noOfNodes.trim());
        Config.noOfNodes = Config.myNodeNumber + 1;

        String[] peerIps;
        int[] peerPorts;

       // System.out.println(noOfNodes);
        switch (noOfNodes.trim()) {
            case "0":
                isSuper = true;
                addMyFiles(4);
                return new RegisterResponse(MessageType.REG_SUCCESS, null, null);
            // break;
            case "1":
                isSuper = true;
                peerIps = new String[1];
                peerPorts = new int[1];
                peerIps[0] = splitted[3];
                peerPorts[0] = Integer.parseInt(splitted[4]);
                addMyFiles(4);
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
                int number = Integer.parseInt(noOfNodes.trim());
                peerIps = new String[number];
                peerPorts = new int[number];
                System.out.println("number:" + number);
                for (int i = 1; i < number + 1; i++) {
                    peerIps[i - 1] = splitted[3 * i];
                    peerPorts[i - 1] = Integer.parseInt(splitted[3 * i + 1]);
                    //System.out.println(peerIps[i - 1] + "," + peerPorts[i - 1]);
                }
                addMyFiles(4);
                return new RegisterResponse(MessageType.REG_SUCCESS, peerIps, peerPorts);

        }
    }

    private void unregister() {
        String message = (new Message(MessageType.UNREG, myIp, myPort, myName)).getMessage();

        Sender.getInstance().sendTCPMessage(message);
    }

    private void sendMessage(String message, String peerIp, int peerPort) {
        //System.out.println("sending message: " + message + " from:" + Config.MY_IP + ":" + Config.MY_PORT + " to:" + peerIp + ":" + peerPort);
        //Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        SenderWebServiceClient.getInstance().sendMessage(message, peerIp, peerPort);
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

    private int getRandomNo(int number, int exception, boolean isSearcherAChild) {
        if (number == 1) {
            if(!isSearcherAChild)
            return -1;
            else return 0;
        }
        int value;
        while ((value = getRandomNo(number)) == exception);
        return value;
    }
    //my files
    //stored as an invereted index
    //format term:set of files
    private HashMap<String, ArrayList<String>> myFiles = new HashMap<>();
    //Store the files children have in key,peers format
    private HashMap<String, ArrayList<String>> chilrensFiles = new HashMap<>();

    public HashMap getMyFiles() {
        return myFiles;
    }

    @Override
    public void update(Observable o, Object arg) {
        //Process incoming message
        //UDPResponse receivedMessage = (UDPResponse) arg;
        String incoming = (String)arg;
        //System.out.println("incoming message:" + incoming);
        String[] msg = incoming.split(" ");
        MessageType msgType = MessageType.valueOf(msg[1]);
        String requesterIp = null;
        int requesterPort = 0;

        switch (msgType) {
            case SEROK:
                String success = msg[2].trim();
                if (!success.equals("0")) {
                    requesterIp = msg[3].trim();
                    requesterPort = Integer.parseInt(msg[4].trim());
                }
                break;

            default:
                requesterIp = msg[2].trim();
                requesterPort = Integer.parseInt(msg[3].trim());
        }
        String info = requesterIp + ":" + requesterPort;
        String outGoingMessage;
        switch (msgType) {
            // for inquire msg : <length INQUIRE IP_address port_no is_super>
            case INQUIRE:
                //System.out.println("Received INQUIRE message");
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

                //joinRequestSentPeers.put(requesterIp, requesterPort);
                break;
            // for join req : <length JOIN IP_address port_no>
            case JOIN:
                if ((msg[4]).startsWith("SUPER")) {
                    System.out.println("Added peer super node:" + (msg[4]).substring(5));
                    superPeers.add(info);
                } else if ((msg[4]).startsWith("NORMAL")) {
                    System.out.println("Added child node:" + (msg[4]).substring(6));
                    childNodes.add(info);
                }
                outGoingMessage = (new Message(MessageType.JOINOK, myIp, myPort, myName)).getMessage();
                sendMessage(outGoingMessage, requesterIp, requesterPort);
                break;
            //for join resp length JOINOK value
            case JOINOK:

                /*value = msg[2].trim();
                 if (value.equals("0")) {
                 info = receivedMessage.getIpAddress() + ":" + receivedMessage.getPort();
                 if (isSuper) {
                 String superPeer = info;
                 superPeers.add(superPeer);
                 System.out.println("Added peer super node: " + info);
                 } else {
                 mySuperNode = info;
                 System.out.println("Added my super node: " + info);
                 }
                 }else if(value.equals("9999")){
                 System.out.println("joining was unsuccessful");*/
                //String joinedPeerIp = receivedMessage.getIpAddress();
                //int joinedPeerPort = joinRequestSentPeers.get(joinedPeerIp);
                //joinRequestSentPeers.remove(joinedPeerIp);
                info = requesterIp + ":" + requesterPort;
                
                if (isSuper) {
                    String superPeer = info;
                    superPeers.add(superPeer);
                    System.out.println("Added peer super node: " + info);
                } else {
                    mySuperNode = info;
                    System.out.println("Added my super node: " + info);

                    //Send the indexed terms for the files I have, to my super peer
                    Iterator iterator = myFiles.keySet().iterator();
                    String myTerms = "";
                    String key;
                    while (iterator.hasNext()) {
                        key = (String) iterator.next();
                        myTerms += "," + key;
                    }
                    outGoingMessage = (new Message(MessageType.FILES, myIp, myPort, myTerms.substring(1))).getMessage();
                    sendMessage(outGoingMessage, requesterIp, requesterPort);
                }
                break;
            case FILES:
                if (!isSuper) {
                    System.out.println("Unexpected request");
                } else {
                    addChildrensFiles(incoming, requesterIp, requesterPort);
                }

                break;
            case SER:
                String[] messageComponents = incoming.split("\"");
                String[] searcherIpPort = messageComponents[0].split(" ");
                String searcherIp = searcherIpPort[2];
                int searcherPort = Integer.parseInt(searcherIpPort[3]);
                String fileKey = messageComponents[1];
                int hopCount;
                //System.out.println("Hop count length:" + messageComponents[2].length());
                try {
                    hopCount = 1 + Integer.parseInt(messageComponents[2].trim());
                } catch (NumberFormatException e) {
                    hopCount = 1;
                }
                //System.out.println("Search message received for key:" + fileKey);
                //check if I have the file
                if (hopCount < Config.TTL) {
                    fileKey = fileKey.toLowerCase();
                    boolean locatable = false;
                    if (myFiles.containsKey(fileKey)) {
                        locatable = true;
                        ArrayList<String> files = myFiles.get(fileKey);
                        int noOfFiles = files.size();
                        //first send the list of files to the searcher
                        // String response = (new Message(MessageType.SEROK, noOfFiles, Config.MY_IP, Config.MY_PORT, hopCount, files, fileKey)).getMessage();
                        String response = (new Message(MessageType.SEROK, noOfFiles, Config.MY_IP, Config.MY_PORT, hopCount, files, fileKey, myIp, myPort)).getMessage();
                        //System.out.println("Created response:" + response);
                        sendMessage(response, searcherIp, searcherPort);
                    }

                    //if I am a super peer, forward the search message to respective peers
                    boolean isSearcherAChild=false;
                    if(childNodes.contains(searcherIp + ":" + searcherPort)){
                        isSearcherAChild=true;
                    }
                    if (isSuper) {
                        //forward the search query to a random peers
                        
                        int randomPeerNumer = getRandomNo(superPeers.size(), superPeers.indexOf(searcherIp + ":" + searcherPort),isSearcherAChild);
                        System.out.println("random peer number:" + randomPeerNumer);
                        String[] ipPort;
                        if (randomPeerNumer != -1) {
                            locatable = true;
                            ipPort = (superPeers.get(randomPeerNumer)).split(":");

                            ////search(fileKey, searcherIp, searcherPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
                            System.out.println("adding to routing table,key:" + ipPort[0] + ipPort[1] + fileKey + "   value:" + searcherIp + ":" + searcherPort);

                            if (routingTable.containsKey(ipPort[0] + ipPort[1] + fileKey)) {
                                innerSet = routingTable.get(ipPort[0] + ipPort[1] + fileKey);
                            } else {
                                innerSet = new HashSet<>();
                            }
                            innerSet.add(searcherIp + ":" + searcherPort);

                            routingTable.put(ipPort[0] + ipPort[1] + fileKey, innerSet);

                            search(fileKey, myIp, myPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);
                        }

                        //next forward the search query to children having the file
                        Iterator it = chilrensFiles.keySet().iterator();
                        while(it.hasNext()){
                            System.out.println("my child has:"+it.next());
                        }
                        if (chilrensFiles.containsKey(fileKey)) {
                            locatable = true;
                            ArrayList<String> peersWithFile = chilrensFiles.get(fileKey);
                            for (String peer : peersWithFile) {
                                ipPort = peer.split(":");
                               // System.out.println("my child has:"+peer);
                                if (routingTable.containsKey(ipPort[0] + ipPort[1] + fileKey)) {
                                    innerSet = routingTable.get(ipPort[0] + ipPort[1] + fileKey);
                                } else {
                                    innerSet = new HashSet<>();
                                }
                                innerSet.add(searcherIp + ":" + searcherPort);

                                routingTable.put(ipPort[0] + ipPort[1] + fileKey, innerSet);
                                //routingTable.put(ipPort[0] + fileKey, searcherIp + ":" + searcherPort);
                                search(fileKey, myIp, myPort, ipPort[0], Integer.parseInt(ipPort[1]), hopCount);

                            }
                        } else {
                            System.out.println("children's files do not contain the key:" + fileKey);
                        }

                        if (!locatable) {
                            //String response = (new Message(MessageType.SEROK, 0)).getMessage();
                            //String response = (new Message(MessageType.SEROK, fileKey)).getMessage();
                            String response = (new Message(MessageType.SEROK, fileKey, myIp, myPort)).getMessage();
                            // System.out.println("Created response:" + response);
                            sendMessage(response, searcherIp, searcherPort);
                        }
                    }
                }
                break;
            case SEROK:
                String[] parts = incoming.split(" ");
                int noOfFiles = Integer.parseInt(parts[2].trim());
                switch (noOfFiles) {
                    case 0:
                        System.out.println("Files not found!");
                        //forwardSEROKToImmediateRequester(incoming, receivedMessage.getIpAddress(),receivedMessage.getPort(),false);
                        System.out.println("incoming:"+incoming);
                        forwardSEROKToImmediateRequester(incoming, parts[4], Integer.parseInt(parts[5].trim()), false);
                        break;
                    case 1:
                       // System.out.println("Files found:");
                        // System.out.println(incoming);
                        //forwardSEROKToImmediateRequester(incoming, receivedMessage.getIpAddress(),receivedMessage.getPort(),true);
                        forwardSEROKToImmediateRequester(incoming, parts[parts.length - 2], Integer.parseInt(parts[parts.length - 1].trim()), true);
                        break;
                    case 9999:
                        System.out.println("Node unreachable");
                        break;
                    case 9998:
                        System.out.println("Unknown error occured...");
                        break;
                    default:
                        //System.out.println("Files found:");
                        //System.out.println(incoming);
                        //forwardSEROKToImmediateRequester(incoming, receivedMessage.getIpAddress(),receivedMessage.getPort(),true);
                        forwardSEROKToImmediateRequester(incoming, parts[parts.length - 2], Integer.parseInt(parts[parts.length - 1].trim()), true);

                }
                break;
            case LEAVE:
                int length = msg.length;
                //if its just a child asking to leave, remove him from the childNodes list 
                //and remove all file names from the super node which were in the leaving node but not in any other children
                if ("CHILD-LEAVING".equals(msg[length].trim())) {
                    //TO-DO: need to remove all file names from the super node which were in the leaving node but not in any other children
                    String leavingChild = msg[2].trim() + ":" + msg[3].trim();
                    Set<String> terms = chilrensFiles.keySet();
                    Iterator<String> iterator = terms.iterator();
                    while(iterator.hasNext()){
                        String currentTerm = iterator.next();
                        ArrayList<String> children = chilrensFiles.get(currentTerm);
                        if (children.contains(leavingChild)){
                            if (children.size() == 1){
                                chilrensFiles.remove(currentTerm);
                            }
                            else{
                                children.remove(leavingChild);
                            }
                        }
                    }
                    childNodes.remove(leavingChild);
                    outGoingMessage = (new Message(MessageType.LEAVEOK,myIp,myPort)).getMessage();
                    sendMessage(outGoingMessage, requesterIp, requesterPort);
                }
                // if is is a super node that is leaving and if it doesn't have two peers
                //this will only be sent to children
                //I have to become a superchild
                else if (length == 3){
                    Config.isSuper = true;
                    isSuper = true;                   
                    outGoingMessage = (new Message(MessageType.LEAVEOK,myIp,myPort)).getMessage();
                    sendMessage(outGoingMessage, requesterIp, requesterPort);
                }
                //if it is a super node that is leaving, take the ip and port it sends and send an INQUIRE message to it asking to connect
                else {
                    outGoingMessage = (new Message(MessageType.LEAVEOK,myIp,myPort)).getMessage();
                    sendMessage(outGoingMessage, requesterIp, requesterPort);
                    String[] ipPort = msg[length].split(":");
                    outGoingMessage = (new Message(MessageType.JOIN, myIp, myPort, "")).getMessage();
                    sendMessage(outGoingMessage, ipPort[0], Integer.parseInt(ipPort[1]));
                }

            case LEAVEOK:
                System.out.println("Peer "+ myName + " is leaving...");
                System.exit(0);
                

        }

    }

//    private boolean isInKeySet(String keyWords, Set<String> keySet){
//        if (!keyWords.contains(" ")){
//            return keySet.contains(keyWords);
//        }
//        else{
//            String[] seperatedKeys = keyWords.split(" ");
//            int count;
//            int length = seperatedKeys.length;
//            boolean byWordMissing = false;
//            String previousKey = seperatedKeys[0];
//            for (count = 1;count < length; count++){
//                if (!keySet.contains(seperatedKeys[count - 1] + " " + seperatedKeys[count])){
//                    return false;
//                }
//            }
//            return true;
//        }
//    }
//    private ArrayList<String> getValuesFromKeySet(String keyWords, HashMap<String,ArrayList<String>> valueSet){
//        if (!keyWords.contains(" ")){
//            return valueSet.get(keyWords);
//        }
//        String[] seperatedWords = keyWords.split(" ");
//        int noOfWords = seperatedWords.length;
//        int count;
//        Set<String> values = new HashSet<>();
//        String previousWord = seperatedWords[0];
//        for (count = 1 ; count < noOfWords ; count++){
//            String byWord = seperatedWords[count - 1] + " " + seperatedWords[count];
//            
//        }
//        return (ArrayList<String>) values;
//    }
    public void start() {

        RegisterResponse response = register();

        if (response.isSucess()) {

          //  Thread reciever = new Thread(Reciever.getInstance());
           // reciever.start();
            WSPublisher.getInstance().publishWebService(myIp, myPort);

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
        //System.out.println("created message" + message);
        //Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
       SenderWebServiceClient.getInstance().sendMessage(message, peerIp, peerPort);
        //System.out.println("Message sent:" + message);
    }

    public void search(String fileName, String searcherIp, int searcherPort, String peerIp, int peerPort) {
        String fileNameString = "\"" + fileName + "\"";
        String message = (new Message(MessageType.SER, searcherIp, searcherPort, fileNameString)).getMessage();
        //System.out.println("created message" + message);
        //Sender.getInstance().sendUDPMessage(message, peerIp, peerPort);
        SenderWebServiceClient.getInstance().sendMessage(message, peerIp, peerPort);
        //System.out.println("Message Sent:" + message);
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
            if (noOfPeers == 1) {
                //select one of my children to be a super peer
                int noOfchildren = childNodes.size();
                int randomChildNo = getRandomNo(noOfchildren - 1);
                message = (new Message(MessageType.LEAVE, myIp, myPort)).getMessage();
                String[] childIpPort = childNodes.get(randomChildNo).split(":");
                String newSuperChild = childNodes.get(randomChildNo);
                sendMessage(message, childIpPort[0], Integer.parseInt(childIpPort[1]));
                for (int i = 0; i < randomChildNo; i++) {
                    message = (new Message(MessageType.LEAVE, myIp, myPort, newSuperChild)).getMessage();
                    ipPort = childNodes.get(i).split(":");
                    sendMessage(message, ipPort[0], Integer.parseInt(ipPort[1]));
                }
                for (int i = randomChildNo; i < noOfchildren; i++) {
                    message = (new Message(MessageType.LEAVE, myIp, myPort, newSuperChild)).getMessage();
                    ipPort = childNodes.get(i).split(":");
                    sendMessage(message, ipPort[0].trim(), Integer.parseInt(ipPort[1].trim()));
                }

                //inform the onlt peer about the newly promoted child
                message = (new Message(MessageType.LEAVE, myIp, myPort, newSuperChild)).getMessage();
                ipPort = superPeers.get(0).split(":");
                sendMessage(message, ipPort[0], Integer.parseInt(ipPort[1]));

            } else {
                for (int i = 0; i < noOfPeers; i++) {
                    ipPort = superPeers.get(i).split(":");
                    if (i < noOfPeers / 2) {
                        //if the index of the super peer in the super peer array list is < length/2 then direct him to join (i+1)th super peer
                        message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i + 1))).getMessage();
                    } else {
                        message = (new Message(MessageType.LEAVE, myIp, myPort, superPeers.get(i - 1))).getMessage();
                    }
                    sendMessage(message, ipPort[0], Integer.parseInt(ipPort[1]));
                }

                //send messages to all children saying I am leaving and give them the ip and port of a super peer to connect with
                int noOfChildren = childNodes.size();
                for (int i = 0; i < noOfChildren; i++) {
                    ipPort = childNodes.get(i).split(":");
                    String randomSuperNode = superPeers.get(getRandomNo(noOfPeers));
                    message = (new Message(MessageType.LEAVE, myIp, myPort, randomSuperNode)).getMessage();
                    sendMessage(message, ipPort[0], Integer.parseInt(ipPort[1]));
                }
            }
        } //if I am not a super Node then just tell the super peer that I am leaving
        else {
            ipPort = mySuperNode.split(":");
            //just pass null to show that I am a normal node
            message = (new Message(MessageType.LEAVE, myIp, myPort, null)).getMessage();
            sendMessage(message, ipPort[0], Integer.parseInt(ipPort[1]));
        }
    }

    private void forwardSEROKToImmediateRequester(String incoming, String senderIp, int senderPort, boolean filesFound) {

        String[] parts = incoming.split(" ");

        String routingTableKey, key, immediateRequesterIpPort;

        Iterator iter;
        String fileString = "", message;
        if (filesFound) {
            key = parts[6];
            fileString = parts[7];
            for (int i = 8; i < parts.length - 2; i++) {
                fileString += " " + parts[i];
            }
        } else {
            key = parts[3];
        }

        //routingTableKey = senderIp + ":" + key;
        routingTableKey = senderIp + senderPort + key;
        System.out.println("checking routing table for the key:" + routingTableKey);
        if (routingTable.containsKey(routingTableKey)) {
            System.out.println("key is inside routing table");
//            immediateRequesterIpPort = routingTable.get(routingTableKey).split(":");
//            routingTable.remove(routingTableKey);
            iter = (routingTable.get(routingTableKey)).iterator();
//            while(iter.hasNext()){
//                immediateRequesterIpPort=(String[])iter.next();
//                sendMessage(incoming, immediateRequesterIpPort[0], Integer.parseInt(immediateRequesterIpPort[1]));
//            }
            if (filesFound) {
                while (iter.hasNext()) {
                    //immediateRequesterIpPort = (String[]) iter.next();
                    immediateRequesterIpPort = (String) iter.next();
                    System.out.println("parts[1]:"+parts[1]+" "+"parts[2]:"+parts[2]+" "+"parts[3]:"+parts[3]+"parts[4]:"+parts[4]);
                    System.out.println("filestring:"+fileString);
                    message = (new Message(MessageType.SEROK, Integer.parseInt(parts[2]), parts[3], Integer.parseInt(parts[4].trim()), Integer.parseInt(parts[5].trim()), fileString, myIp, myPort)).getMessage();
                    sendMessage(message, immediateRequesterIpPort.split(":")[0], Integer.parseInt(immediateRequesterIpPort.split(":")[1]));
                }
            } else {
                while (iter.hasNext()) {
                    immediateRequesterIpPort = (String) iter.next();
                    message = (new Message(MessageType.SEROK, key, myIp, myPort)).getMessage();
                    sendMessage(message, immediateRequesterIpPort.split(":")[0], Integer.parseInt(immediateRequesterIpPort.split(":")[1]));
                }
            }
            routingTable.remove(routingTableKey);

            //sendMessage(incoming, immediateRequesterIpPort[0], Integer.parseInt(immediateRequesterIpPort[1]));
        } else {
            System.out.println("key is not inside routing table");
            // System.out.println("*Files found");
        }

    }

}
