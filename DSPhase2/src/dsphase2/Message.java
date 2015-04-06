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
    
    public Message(MessageType type, String ip, int port, String name,boolean isSuper,int success){
        char para = 'F';
        if(isSuper){
            para = 'T'; 
        }
        switch(type){
            case REG:message = appendLength("REG"+" "+ip+" "+port+" "+name);
                break;
            case UNREG:message=appendLength("UNREG"+" "+ip+" "+port+" "+name);
                break;
            case JOIN:message=appendLength("JOIN"+" "+ip+" "+port);
                break;
            case JOINOK: appendLength("JOINOK"+" "+success);
                break; 
            case INQUIRE: message= appendLength("INQUIRE"+" "+ip+" "+port+" "+para);
                break; 
            case INQUIREOK: message= appendLength("INQUIREOK"+" "+ip+" "+port);
                break; 
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

