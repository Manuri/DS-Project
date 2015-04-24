/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dsphase2;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 *
 * @author Amaya
 */
public class SenderWebServiceClient {
    
    private static SenderWebServiceClient instance=null;
    
    private SenderWebServiceClient(){
    }
    
    public static SenderWebServiceClient getInstance(){
        if(instance==null){
            return new SenderWebServiceClient();
        }
        else{
            return instance;
        }
    }

    public static void testWebService(String ip, int port, String aString) {

        String urlString = "http://" + ip + ":" + port + "/ws/netlingoservice?wsdl";
        try {
            URL url = new URL(urlString);

            QName qname = new QName("http://dsphase2/", "WSInterfaceImplService");

            Service service = Service.create(url, qname);

            ReceiverWebService netlingoService = service.getPort(ReceiverWebService.class);

            System.out.println(netlingoService.getHelloWorldAsString("Amaya"));

        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException. Problem in url string");
        }
    }
    
    public void sendMessage(String message, String peerIp, int peerPort){
        String urlString = "http://" + peerIp + ":" + peerPort + "/ws/netlingoservice?wsdl";
        
        try {
            URL url = new URL(urlString);

            QName qname = new QName("http://dsphase2/", "ReceiverWebServiceImplService");

            Service service = Service.create(url, qname);

            ReceiverWebService netlingoService = service.getPort(ReceiverWebService.class);

            netlingoService.receiveMessage(message);

        } catch (MalformedURLException ex) {
            System.out.println("MalformedURLException. Problem in url string");
        }
    }

    public static void main(String[] args) throws Exception {

       //testWebService("localhost",9999,"Amaya");
        //sendMessage("My name is Amaya","localhost", 9999);
    }
}
