/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Amaya
 */
public class Network {
    public static void main(String[] args) {
        
        try {
            //When configured as normal node
            Node n1 = Node.getInstance(Config.MY_IP,Config.MY_PORT,Config.MY_NAME);
            n1.start();
            
            Thread.sleep(2000);
            n1.search("Windows");
        } catch (InterruptedException ex) {
            Logger.getLogger(Network.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
