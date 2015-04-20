/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import javax.swing.UIManager;


/**
 *
 * @author Amaya
 */
public class Network implements Runnable {

        
    static ConfigWindow configWindow; 
    @Override
    public void run() {
                 //When configured as normal node
             Config.CONFIG_WINDOW = configWindow; 
//           Node n1 = Node.getInstance(Config.MY_IP,Config.MY_PORT,Config.MY_NAME);
//            n1.start();
            //Thread.sleep(2000);
          //  n1.search("Windows");
    }
    
    public static void main(String[] args) {
        
    
                /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }
                ConfigWindow configWindow = new ConfigWindow();
                Config.CONFIG_WINDOW = configWindow; 
                configWindow.setVisible(true);
            }
            
           
           
            
            
        });

//            Node n1 = Node.getInstance(Config.MY_IP,Config.MY_PORT,Config.MY_NAME);
//            n1.start();
//            //Thread.sleep(2000);
//          //  n1.search("Windows");
        
    }
}
