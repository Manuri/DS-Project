/*
 * CS4262 Distributed Systems Mini Project
 */

package dsphase2;

/**
 *
 * @author Sasikala Kottegoda
 */
public class UDPResponse {
    private final String data;
    private final String ipAddress;
    private final int port;

    public UDPResponse(byte[] someData, String anIp, int aPort){
        data = new String(someData);
        ipAddress = anIp;
        port = aPort;
    }
    
    public String getData() {
        return data;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
    
}
