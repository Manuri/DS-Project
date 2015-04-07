/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

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
            case JOIN:message=appendLength("JOIN"+" "+ip+" "+port);
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
        }
    }
    
    public Message(MessageType type, int success){
        switch(type){
            case JOINOK: message=appendLength("JOINOK"+" "+success);
                break;
        }
    }
    
    public Message(MessageType type, String ip, int port, String fileNanme, int maxHops){
        message=appendLength("SER"+" "+ip+" "+port+" "+fileNanme+" "+maxHops);
    }
    
    public Message(MessageType type, int noOfFiles, String ip, int port, int hops, String[] files){
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

