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
        SuperNode sn = new SuperNode("localhost",9876);
        
        sn.register();
    }
}
