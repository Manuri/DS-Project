/*
 * CS4262 Distributed Systems Mini Project
 */

package com.netlingoclient.classes;

/**
 *
 * @author Amaya
 */
public class RegisterResponse {
    private final MessageType message;
    private final String[] peerIps;
    private final int[] peerPorts;
    
    public RegisterResponse(MessageType msg,String[] peerIps,int[] peerPorts){
        this.message=msg;
        this.peerIps=peerIps;
        this.peerPorts=peerPorts;
    }
    
    public Boolean isSucess(){
        if(message==MessageType.REG_SUCCESS){
            return true;
        }
        else{
            return false;
        }
    }
    
    public String[] getPeerIps(){
        return peerIps;
    }
    
    public int[] getpeerPorts(){
        return peerPorts;
    }
    
    public Boolean isInitialNode(){
        if(message==MessageType.REG_SUCCESS && getPeerIps()==null&& getpeerPorts()==null){
            return true;
        }
        else return false;
    }
}
