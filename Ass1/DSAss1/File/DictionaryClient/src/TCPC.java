import java.io.*;
import java.net.*;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class TCPC {
    static Socket socket = null;

    public static String connect(String host, int port, String request, String word, String meaning) {
        String received = "no message";
        JSONObject json = new JSONObject();
        try {
            socket = new Socket(host, port);
            System.out.println("Connection established");
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            switch (request.toUpperCase()) {
                case "SEARCH":
                	json.put("request", "SEARCH");
                	json.put("word", word);
                	output.writeUTF(json.toString());
                    output.flush();
                    String mess = input.readUTF();
                    return received = mess;
                case "ADD":
                	json.put("request", "ADD");
                	json.put("word", word);
                	json.put("meaning", meaning);
                	output.writeUTF(json.toString());
                    String mess1 = input.readUTF();
                    return received = mess1;
                case "DELETE":
                	json.put("request", "DELETE");
                	json.put("word", word);
                	output.writeUTF(json.toString());
                    String mess11 = input.readUTF();
                    return received = mess11;
                case "EXIT":
                    socket.close();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
        	received = "Failed to transfer json type!";
        } catch (UnknownHostException e) {
            received = "Unknown host!";
        } catch (FileNotFoundException e){
        	received = "Wrong file name!";
        }catch (IOException e) {
            received = "Socket closed!";
        } 
        return received;
    }    
}
