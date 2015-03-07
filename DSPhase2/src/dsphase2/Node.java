/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;


/**
 *
 * @author Amaya
 */
public class Node {
    private final String ip;
    private final int port;
    
    Node(String ip,int port){
        this.ip=ip;
        this.port=port;   
    }
    
    public String getIp(){
        return ip;
    }
    
    public int getPort(){
        return port;
    }
    public void register(){
        
    }
}
