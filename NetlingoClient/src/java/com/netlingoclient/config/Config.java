/*
 * CS4262 Distributed Systems Mini Project
 */

package com.netlingoclient.config;

import java.util.ArrayList;

/**
 *
 * @author Amaya
 */
public class Config {
    
    //Whe  configured as normal node
//    static final String MY_IP = "127.0.0.1";
//    static final int MY_PORT = 5001;
//    static final String MY_NAME = "Devni";
//    static final boolean isSuper = true;
    
    //When configured as peer
    
    
    //static final String MY_IP = "127.0.0.2";
    //static final int MY_PORT = 5002;
    //static final String MY_NAME = "Sasikala";
    //static final boolean isSuper = false;
    
    public static ConfigWindow CONFIG_WINDOW; 
    
    public static  String MY_IP = "10.8.108.142";

    public static  int MY_PORT = 5002;

    public static  String MY_NAME = "Amaya";
    public static  boolean isSuper = true;
    

    public static  String BOOTSTRAP_IP = "10.8.108.169";


    public static  int BOOTSTRAP_PORT =  9876;
    public static ArrayList<String> availableFiles = new ArrayList<>(); 
   
    public static int TTL = 5;
    public void addNewFile(String fileName){
        availableFiles.add(fileName); 
    }
    
    
     
    
    
}
