/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.util.ArrayList;

/**
 *
 * @author Amaya
 */
public class Message {
    private String message;
    
    public Message(MessageType type, String ip, int port, String name){

        switch(type){
            case REG:message = appendLength("REG"+" "+ip+" "+port+" "+name);
                break;
            case UNREG:message=appendLength("UNREG"+" "+ip+" "+port+" "+name);
                break;
            case JOIN:message=appendLength("JOIN"+" "+ip+" "+port+" "+name);
                break;
            case JOINOK: message=appendLength("JOINOK"+" "+0);
                break;
            case SER:
            {
                String fileName = name;
                message=appendLength("SER"+" "+ip+" "+port+" "+fileName);
                break;
            }
            case INQUIRE: message= appendLength("INQUIRE"+" "+ip+" "+port);
                break; 
            case INQUIREOK: message= appendLength("INQUIREOK"+" "+ip+" "+port);
                break;
            case LEAVE:
                String peerIpPort = name;
                if(peerIpPort!=null){
                    message=appendLength("LEAVE"+" "+ip+" "+port+" "+name);
                }else{
                    message=appendLength("LEAVE"+" "+ip+" "+port+" "+"CHILD-LEAVING");
                }
                break;
        }
    }
    
    public Message(MessageType type, int success){
        switch(type){
            case JOINOK: message=appendLength("JOINOK"+" "+success);
                break;
        }
    }
    
    public Message(MessageType type, String ip, int port, String fileNanme, int hops){
        message=appendLength("SER"+" "+ip+" "+port+" "+fileNanme+" "+hops);
    }
    
    public Message(MessageType type, int noOfFiles, String ip, int port, int hops, ArrayList<String> files){
        switch(type){
        
            case SEROK: 
            {
                String filesString="";
                for (String file : files) {
                    filesString = filesString +" "+ file;
                }
                message=appendLength("SEROK"+" "+noOfFiles+" "+ip+" "+port+" "+hops+" "+filesString.substring(1));
                break;
        
            }
        }
    }
    
        public Message(MessageType type, int noOfFiles, String ip, int port, int hops, String fileString){
        switch(type){
        
            case SEROK: 
            {
                message=appendLength("SEROK"+" "+noOfFiles+" "+ip+" "+port+" "+hops+" "+fileString);
                break;
        
            }
        }
    }
    public String getMessage(){
        return message;
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
    
    
}

