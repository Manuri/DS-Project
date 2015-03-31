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
        Node n1 = Node.getInstance("129.82.123.47",5003,"B");
        //Node n1 = Node.getInstance("129.82.123.47",5004,"F");
       // Node n1 = Node.getInstance("129.82.123.47",5005,"G");
        //Node n1 = Node.getInstance("129.82.123.47",5004,"H");
        
        n1.start();
        
        
    }
}
