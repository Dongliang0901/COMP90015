import java.io.*;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Server {
	
    public static void main(String[] args) {
        ServerSocket listeningSocket = null;
        Socket clientSocket = null;
        ServerView.view();
        Log log =LogFactory.getLog(ServerThread.class);
        try {
            listeningSocket = new ServerSocket(1234);
            while (true) {
                log.info("Server listening on port "+ 1234 +" for a connection\n");
                clientSocket = listeningSocket.accept();
                ServerThread serverThread = new ServerThread(clientSocket, "G:\\COMP90015\\Ass1\\DSAss1\\File\\Server\\Dictionary.json");
                serverThread.start();
            }
        }
        catch (SocketException ex) {
            ex.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(listeningSocket != null) {
                try {
                    listeningSocket.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }    
}
