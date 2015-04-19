/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import static dsphase2.Network.configWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sasikala Kottegoda
 */
public class NodeTest {
    
    private Node node;
    public NodeTest() {
        Config.CONFIG_WINDOW = configWindow; 
        Node n1 = Node.getInstance(Config.MY_IP,Config.MY_PORT,Config.MY_NAME);
        //n1.start();
        System.out.println("I started");
    }
    
    @BeforeClass
    public static void setUpClass() {
            
        //Thread.sleep(2000);
            
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class Node.
     */
    @Test
    public void testGetInstance() {
        HashMap<String,ArrayList<String>> index = node.getMyFiles();
        Set<String> terms = index.keySet();
        for (Iterator<String> it = terms.iterator(); it.hasNext();) {
            String term = it.next();
            ArrayList<String> files = index.get(term);
            System.out.println(term);
            for (String file : files){
                System.out.println("\t" + file);
            }
        }
    }

    /**
     * Test of getIp method, of class Node.
     */
    @Test
    public void testGetIp() {
        System.out.println("getIp");
        Node instance = null;
        String expResult = "";
        String result = instance.getIp();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPort method, of class Node.
     */
    @Test
    public void testGetPort() {
        System.out.println("getPort");
        Node instance = null;
        int expResult = 0;
        int result = instance.getPort();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of register method, of class Node.
     */
    @Test
    public void testRegister() {
        System.out.println("register");
        Node instance = null;
        RegisterResponse expResult = null;
        RegisterResponse result = instance.register();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of update method, of class Node.
     */
    @Test
    public void testUpdate() {
        System.out.println("update");
        Observable o = null;
        Object arg = null;
        Node instance = null;
        instance.update(o, arg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of start method, of class Node.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        Node instance = null;
        instance.start();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of search method, of class Node.
     */
    @Test
    public void testSearch_String() {
        System.out.println("I am searching");
//        
//        node.search("Windows");
//        
//        System.out.println("search");
//        String fileName = "";
//        Node instance = null;
//        instance.search(fileName);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    /**
     * Test of search method, of class Node.
     */
    @Test
    public void testSearch_4args() {
        System.out.println("search");
        String fileName = "";
        String peerIp = "";
        int peerPort = 0;
        int hopCount = 0;
        Node instance = null;
        instance.search(fileName, peerIp, peerPort, hopCount);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of search method, of class Node.
     */
    @Test
    public void testSearch_6args() {
        System.out.println("search");
        String fileName = "";
        String searcherIp = "";
        int searcherPort = 0;
        String peerIp = "";
        int peerPort = 0;
        int hopCount = 0;
        Node instance = null;
        instance.search(fileName, searcherIp, searcherPort, peerIp, peerPort, hopCount);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of search method, of class Node.
     */
    @Test
    public void testSearch_5args() {
        System.out.println("search");
        String fileName = "";
        String searcherIp = "";
        int searcherPort = 0;
        String peerIp = "";
        int peerPort = 0;
        Node instance = null;
        instance.search(fileName, searcherIp, searcherPort, peerIp, peerPort);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of search method, of class Node.
     */
    @Test
    public void testSearch_3args() {
        System.out.println("search");
        String fileName = "";
        String peerIp = "";
        int peerPort = 0;
        Node instance = null;
        instance.search(fileName, peerIp, peerPort);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of UpdateTheLog method, of class Node.
     */
    @Test
    public void testUpdateTheLog() {
        System.out.println("UpdateTheLog");
        String msg = "";
        Node instance = null;
        instance.UpdateTheLog(msg);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
