import java.io.*;
import java.net.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

public class ServerThread extends Thread{

    static Socket socket = null;
    static String filePath;
    Log log =LogFactory.getLog(ServerThread.class);
    ServerThread(Socket socket, String filePath){
        this.socket = socket;
        this.filePath = filePath;
    }

    public void run(){
        try {
            while(true) {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                String message = null;
                while ((message = input.readUTF()) != null) {
                	JSONObject json = new JSONObject(message);
                	switch (json.getString("request")) {
					case "SEARCH":
						log.info("The client search the meaning of " + json.getString("word") + ".\n");
						break;
					case "ADD":
						log.info("The client add the meaning of " + json.getString("word") + ". The meaning of the new word is: " + json.getString("meaning")+".\n");
						break;
					case "DELETE":
						log.info("The client delete the meaning of " + json.getString("word") + ".\n");
					default:
						break;
				} 
                    String out = Dictionary.dictionary(json, filePath);
                    output.writeUTF(out);
                    output.flush();
                }
            }
        }catch(SocketException e) {
            log.info("closed...\n");
        }catch (IOException e){
            log.info("opps...\n");
        }catch(Exception e){
        	log.info("client invalid input!\n");
        }
    }
    
    public static String Logger(){
    	String content = null;
    	String pathname = "logRecord.txt";
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader)
        ) {
            String line;
            while ((line = br.readLine()) != null) {
            	content += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    	return content;
    }
    
}


