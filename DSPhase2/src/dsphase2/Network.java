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
       // Node n1 = Node.getInstance("129.82.123.47",5004,"F");
       //Node n1 = Node.getInstance("129.82.123.47",5005,"G");
       // Node n1 = Node.getInstance("129.82.123.47",5004,"H");
        
        //Node n1 = Node.getInstance("129.82.123.45",5001,"1234abcd");
        //Node n1 = Node.getInstance("127.0.0.1",5010,"Amaya");
        //Node n1 = Node.getInstance("127.0.0.1",5011,"A");
        //Node n1 = Node.getInstance("127.0.0.1",5012,"C");
        //Node n1 = Node.getInstance("127.0.0.1",5013,"J");
        n1.start();
        
        
    }
}
