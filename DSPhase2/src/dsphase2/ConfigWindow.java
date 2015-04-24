/*
 * CS4262 Distributed Systems Mini Project
 */
package dsphase2;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Dinithi
 */
public class ConfigWindow extends JFrame implements Observer {

    /**
     * Component declaration
     */
    private JButton jButtonJoin, jButtonLeave, jButtonSearch;
    private JLabel jLabel1, jLabel10, jLabel11, jLabel12, jLabel13, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8;
    private JRadioButton jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4;
    private JSeparator jSeparator1;
    private JTextArea jTextArea1;
    private JTextField jTextField1, jTextField2, jTextField3, jTextField4, jTextField5, jTextField6;
    private JScrollPane jScrollPane1, jScrollPane2;
    private JPanel jPanel1;
    private JList jList1;

    Node n1;

    /**
     * Creates new form ConfigWindow
     */
    public ConfigWindow() {
        this.setTitle("Netlingo File Sharing System: Config Window");
        System.out.println("hello");
        initComponents();

        ButtonGroup nodeTypeGroup = new ButtonGroup();
        jRadioButton1.setSelected(true);
        nodeTypeGroup.add(jRadioButton1);
        nodeTypeGroup.add(jRadioButton2);

        ButtonGroup communicationModeGroup = new ButtonGroup();
        jRadioButton3.setSelected(true);
        communicationModeGroup.add(jRadioButton3);
        communicationModeGroup.add(jRadioButton4);

        this.setResizable(false);

    }

    @Override
    public void update(Observable o, Object arg) {

        if (arg instanceof UDPResponse) {
            jTextArea1.append("[Netlingo]: " + ((UDPResponse) arg).getData().trim() + " [" + getTimeStamp() + "] \n");
        } else {
            jTextArea1.append("[Netlingo]: " + ((String) arg) + " [" + getTimeStamp() + "] \n");
        }
    }

    private void initComponents() {

        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        jTextField1 = new JTextField();
        jLabel4 = new JLabel();
        jLabel5 = new JLabel();
        jLabel8 = new JLabel();
        jLabel10 = new JLabel();
        jLabel11 = new JLabel();
        jLabel12 = new JLabel();
        jLabel13 = new JLabel();
        jTextField2 = new JTextField();
        jTextField3 = new JTextField();

        jRadioButton1 = new JRadioButton();
        jRadioButton2 = new JRadioButton();
        jRadioButton3 = new JRadioButton();
        jRadioButton4 = new JRadioButton();
        jTextField4 = new JTextField();
        jTextField5 = new JTextField();
        jButtonJoin = new JButton();
        jButtonLeave = new JButton();
        jLabel6 = new JLabel();
        jButtonSearch = new JButton();
        jScrollPane1 = new JScrollPane();
        jTextArea1 = new JTextArea();
        jTextField6 = new JTextField();
        jLabel7 = new JLabel();
        jSeparator1 = new JSeparator();
        jScrollPane2 = new JScrollPane();

        jList1 = new JList();

        try {
            InputStreamReader in = new InputStreamReader(getClass().getResourceAsStream("/resources/FileNames"), "UTF-8");
            Scanner s = new Scanner(in).useDelimiter("\n");
            final ArrayList<String> fileNames = new ArrayList<>();
            while (s.hasNext()) {
                fileNames.add(s.next());
            }
            final String[] list = new String[fileNames.size()];
            for (int i = 0; i < fileNames.size(); i++) {
                list[i] = fileNames.get(i);
            }
            jList1.setModel(new AbstractListModel() {
                String[] strings = list;

                public int getSize() {
                    return strings.length;
                }

                public Object getElementAt(int i) {
                    return strings[i];
                }
            });
            jScrollPane2.setViewportView(jList1);
            //  jComboBox1.setModel(new DefaultComboBoxModel(fileNames.toArray()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(16, 35, 75));
        jLabel1.setText("Netlingo File Sharing System 1.0");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Client Configuration");

        jLabel3.setText("IP Address");

        jTextField1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        jLabel4.setText("Port no");

        jLabel5.setText("Username");

        jLabel8.setText("Available Files");

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 12));

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel11.setText("Bootstrap Server Configuration");

        jLabel12.setText("IP Address");

        jLabel13.setText("Port no");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        jRadioButton1.setText("Super node");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
            }
        });

        jRadioButton2.setText("Ordinary node");

        jRadioButton3.setText("Web service");

        jRadioButton4.setText("Sockets");

        // joining action
        jButtonJoin.setText("Join the Network");
        jButtonJoin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {

                Config.MY_IP = jTextField1.getText();
                Config.MY_PORT = Integer.parseInt(jTextField2.getText());
                Config.MY_NAME = jTextField3.getText();
                Config.BOOTSTRAP_IP = jTextField4.getText();
                Config.BOOTSTRAP_PORT = Integer.parseInt(jTextField5.getText());
                Config.isSuper = false;
                boolean isWebServices = false;
                if (jRadioButton1.isSelected()) {
                    Config.isSuper = true;
                }
                Config.isWebService = false;
                if (jRadioButton3.isSelected()) {
                    Config.isWebService = true;
                }
                n1 = Node.getInstance(Config.MY_IP, Config.MY_PORT, Config.MY_NAME);
                n1.start();

            }
        });

        jTextField1.setText(Config.MY_IP);
        jTextField2.setText(Integer.toString(Config.MY_PORT));
        jTextField3.setText(Config.MY_NAME);
        jTextField4.setText(Config.BOOTSTRAP_IP);
        jTextField5.setText(Integer.toString(Config.BOOTSTRAP_PORT));

        jButtonLeave.setText("Leave the Network");
        jButtonLeave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                n1.leave();
            }

        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel6.setText("Mode of Execution");

        jButtonSearch.setText("Search the Network");
        jButtonSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                n1.search(jTextField6.getText());

            }
        });

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        Font font1 = new Font("Tahoma", 0, 11);
        //    Font font = new Font("Tahoma", 0 , 11);
        jTextArea1.setFont(font1);
        jTextArea1.setForeground(Color.BLUE);
        // jTextArea1.setBackground(Color.black);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setWrapStyleWord(true);
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        jTextArea1.append("[Netlingo]: Welcome to Demo Log View [" + getTimeStamp() + "]\n");

        jScrollPane1.setViewportView(jTextArea1);

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("File Name");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel11).addComponent(jLabel10).addComponent(jLabel2).addComponent(jLabel1).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel5).addComponent(jLabel3)).addGap(42, 42, 42).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE).addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(8, 8, 8).addComponent(jLabel4).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jRadioButton2).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jRadioButton1).addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))))).addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jButtonJoin).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonLeave)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabel12).addGap(42, 42, 42).addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jRadioButton3).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jRadioButton4))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup().addComponent(jLabel13).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)).addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE).addComponent(jButtonSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))).addGap(129, 129, 129)).addComponent(jLabel6)).addContainerGap()));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGap(25, 25, 25).addComponent(jLabel1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(2, 2, 2).addComponent(jLabel2).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel5).addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3).addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel4)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel8).addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel11)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jRadioButton1).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jRadioButton2))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel12).addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel13).addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(18, 18, 18).addComponent(jLabel6).addGap(5, 5, 5).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addGroup(jPanel1Layout.createSequentialGroup().addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel10).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton4).addComponent(jRadioButton3))).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addGap(6, 6, 6).addComponent(jLabel7).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(6, 6, 6))).addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButtonJoin).addComponent(jButtonLeave).addComponent(jButtonSearch)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

        pack();
    }// </editor-fold>

    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {

                ConfigWindow configWindow = new ConfigWindow();
                Config.CONFIG_WINDOW = configWindow;
                try {
                    UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                }
                new ConfigWindow().setVisible(true);

            }
        });
    }

    private String getTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
    }
}
//IPadd textf1
//port 	2
//username 3
//bs ip  4
//bsportno 5
//searchfile name 6
//super node rb1
//ord. rb2
//webservice rb3
//socket rb4
//
//join but 1
//leave 2
//search 3
