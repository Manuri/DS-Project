/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

/**
 *
 * @author Amaya
 */
public class Network {
    public static void main(String[] args) {
        Node n1 = Node.getInstance(Config.MY_IP,Config.MY_PORT,Config.MY_NAME);

        n1.start();
        
        
    }
}
