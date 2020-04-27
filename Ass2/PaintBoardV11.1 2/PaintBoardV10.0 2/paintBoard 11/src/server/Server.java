
package server;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.DefaultListModel;

import org.json.JSONObject;

import client.ClientBean;
public class Server implements Runnable {// Server
    static List<Socket> socketList=new ArrayList<Socket>();
    public static int port;
    public static String inputXML;
    public static int clientNum;
    public static int countClient = 0;
    JSONObject json = new JSONObject();
// Read In
    static Socket socket = null;
    static ServerSocket serverSocket = null;
    
    List<String> lstChat = new ArrayList<String>();
    List<String> lstUsers = new ArrayList<String>();
    
    public Server() {// Constructor
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
        	System.out.println("Server stopped!");
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
    	port = 9999;
    	inputXML = "dictionary.xml";
        

        System.out.println("************Server Start*************");
        Server t = new Server();
        int count = 0;
        while (true) {          
            try {
                socket = serverSocket.accept();
                count++;
                //System.out.println("Client " + count + "has connected");
                socketList.add(socket);
                countClient++;
                System.out.println(countClient);
            } catch (IOException e) {
                // TODO Auto-generated catch block
            	//System.out.println("Client" + count + " has dicnonnected!");
                e.printStackTrace();
            }
            Print p = new Print(socket);
            Thread read = new Thread(t);
            Thread print = new Thread(p);
            read.start();
            print.start();
        }
    }

    @Override
    public void run() {
    	String userName = "";
    	String message = "";
    	int countDelete = 0;
    	String ans = "";
    	List<String> meaningsAdd = new ArrayList<String>();
    	int countInitMeaning = 0;
        // Override run method
        try {
            Thread.sleep(1000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            while (true) {
                String input = in.readLine();
                if(input.startsWith("newuser")) {				//鍚戞湰鍦扮敤鎴峰垪琛ㄦ坊鍔犳柊鐢ㄦ埛
                	String msg = input.substring(8);
                	System.out.println("Server line 99: " + msg);
                	lstUsers.add(msg);
                	input = "newuser;" + lstUsers.toString();
                	System.out.println(lstUsers);
                }
                else if (input.startsWith("chat")) {		//鍚戞湰鍦拌亰澶╄褰曟坊鍔犳柊鑱婂ぉ
                	String msg = input.substring(4);
                	//System.out.println("111"+msg);
                	lstChat.add(msg); 
                	
                }
                else if  (input.startsWith("kick")) {		//浠庢湰鍦扮敤鎴峰垪琛ㄥ綍鍒犻櫎琚涪鐢ㄦ埛
            		String username = input.substring(5);
            		lstUsers.remove(username);
            		input = "list;"+ lstUsers.toString(); // input更新为除去用户后的list
            	}
//                else if (input.startsWith("newuser")) {
//               input = "newuser;" + lstUsers.toString();
//                }
                

                
                /**********************Send message to client***************************/
                for (int i = 0; i < socketList.size(); i++) {               	
                    Socket socket=socketList.get(i);
                    System.out.println(socket.getPort());
                    PrintWriter out = new PrintWriter(socket.getOutputStream());
                    //if (socket!=this.socket) {
                    	out.println(input);
                    	out.flush();
                    //socket.shutdownOutput();
                    //}
                }
            }
        } catch (Exception e) {
        	JOptionPane.showMessageDialog(null, "Client has disconneted!");
        	countClient -- ;
//            e.printStackTrace();
        }
    }
}
class Print implements Runnable {
    static List<Socket> socketList=new ArrayList<Socket>();
    Scanner input = new Scanner(System.in);
    public Print(Socket s) {// Constructor
        try {
            socketList.add(s);
        } catch (Exception e) {
        	System.out.println("Socket failed!");
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            //Thread.sleep(100);
            while (true) {
                String msg = input.next();
            for (int i = 0; i < socketList.size(); i++) {
                Socket socket=socketList.get(i);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(msg);
                out.flush();
            }
            }
        } catch (Exception e) {
            // TODO: handle exception
        	System.out.println("Flush failed!");
            e.printStackTrace();
        }
    }
}
