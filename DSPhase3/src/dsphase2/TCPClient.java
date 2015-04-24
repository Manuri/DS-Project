/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

import java.io.*;
import java.net.*;

/*
    This is a test class
*/
class TCPClient
{
 public static void main(String argv[]) throws Exception
 {   
     
     String sentence;
  
    String modifiedSentence;

    BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));

    Socket clientSocket = new Socket("localhost", 9876);

    PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(),true);

    BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  
   outToServer.println("0036 REG 129.82.123.45 5001 1234abcd");       

  char[] buf=new char[100];
  
   inFromServer.read(buf);
   System.out.println(buf);
    
  outToServer.close();
  inFromServer.close();
  
  clientSocket.close(); 

 }
}
