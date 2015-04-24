/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dsphase2;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

/**
 *
 * @author Amaya
 */
@WebService
@SOAPBinding(style = Style.RPC)
public interface ReceiverWebService {
    
    @WebMethod String getHelloWorldAsString(String name);
    
    @WebMethod void receiveMessage(String message);
}
