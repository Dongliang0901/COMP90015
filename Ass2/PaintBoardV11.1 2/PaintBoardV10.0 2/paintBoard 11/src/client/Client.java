package client;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;

import org.json.JSONObject;

import client.ClientBean;
import client.Paint.DrawType;

public class Client implements Runnable {// Client
	
	public String StringMsg;
    private Socket socket = null;
    Scanner input = new Scanner(System.in);
    String name=null;
    
    public Client (Socket socket,String name) {
		this.socket = socket;
		this.name = name;
    }
    
    public void SetMsg(String msg, Socket socket) {
    	this.StringMsg = msg;
    	this.socket = socket;
    }
    
    public void SetSocket(Socket socket) {
    	this.socket = socket;
    }
    
    @Override
    public void run() {
        try {     
            PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(Paint.shapeType+ ";" + StringMsg);
                out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
class Read implements Runnable {
	static DefaultListModel<String> lm=new DefaultListModel<String>();
    static Socket socket = null;
    public String result;
    String name=null;
    JSONObject json = new JSONObject();
    
    public String getResult() {
    	return this.result;
    }
    
    public Read(Socket socket, String name) {
        this.socket = socket;
        this.name = name; 
    }
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
            	String para;
            	result = in.readLine();
            	//System.out.println(result);
            	String type = result.split(";")[0];
            	if(result.split(";").length > 1) {
            		para = result.split(";")[1];
            	}else {
            		para = null;
            	}
            	if(para.split("&")[0].equals("close")) {
            		System.exit(0);
            	}
            	else if(para.split("&")[0].equals("SYNC")) {
            		if(Paint.isConnect == 1) {
            			Paint.receiveImageAndShow(para.split("&")[1]);
            		}
            	}
            	
            	else if(para.split("&")[0].equals("AskForConnect")) {
            		if(Paint.isManager == 1) {
            			Paint.acceptConnection(para.split("&")[1]);
            			System.out.println("Ask for connection");
            		}
            	}else if(para.split("&")[0].equals("ReplyRequest")) {
            		if(Paint.countClient == Integer.parseInt(para.split("&")[1])) {
            			Paint.connectionSuccess(Integer.parseInt(para.split("&")[2]));
            			//System.out.println("Ask for connection");
            		}
            	}else if(para.split("&")[0].equals("RequestImage")) {
            		if(Paint.isManager == 1) {
            			Paint.sendImageToClient(para.split("&")[1]);
            		}
            	}else if(para.split("&")[0].equals("SendImage")) {
            		if(Paint.isConnect == 1) {
            			Paint.receiveImageAndShow(para.split("&")[2]);
            		}
            	}
            	else if(para.split("&")[0].equals("image") && (Paint.isConnect == 1|| Paint.isManager == 1)){
            		Image image = Paint.generateImage(para.split("&")[1]);
            		Paint.graphics2D.drawImage(image, 0, 0, null);
            		Paint.canvas.repaint();
            	}
            	else if(para.equals("clean")&& (Paint.isConnect == 1|| Paint.isManager == 1)) {
            		Paint.cleanBoard(Paint.graphics2D);
            	}
            	else if(type.equals("TEXT")&& (Paint.isConnect == 1|| Paint.isManager == 1)) {
            		String color = para.split("&")[0];
            		String text = para.split("&")[1];
            		int x = Integer.parseInt(para.split("&")[2]);
            		int y = Integer.parseInt(para.split("&")[3]);
            		Paint.remoteText(color, text, x, y);
            	}
            	else if(para.split("&")[0].equals("json") && (Paint.isConnect == 1|| Paint.isManager == 1)){
            		json = new JSONObject(para.split("&")[1]);
            		Paint.remoteDraw(json.getString("Type"), json.getInt("thick"), json.getString("color"), json.getInt("fill"),
                			json.getInt("pos1"), json.getInt("pos2"), json.getInt("pos3"), json.getInt("pos4"));
            	}
				else if(type.equals("newuser")) {//得到的用户名存进用户列表模型dlm
						System.out.println("Manager added");
                        DefaultListModel<String>  dlm=new DefaultListModel<String>();
                        
                        StringTokenizer st=new StringTokenizer(para,"[,]");
                        //System.out.println(para);
                        while(st.hasMoreTokens())
                        {
                           dlm.addElement(st.nextToken().trim());
                        }
                        
                        Paint.lstUsers.setModel(dlm);
                        //System.out.println(dlm);
                        System.out.println("Line 117" + Paint.lstUsers);
                    } 
            	else if(type.equals("chat")) {//得到的来自用户的文字显示在聊天框上并换行，其中用户名存进本地
            		System.out.println(para);
            		String usn = para.split("&")[0];
            		
                	String txt = para.split("&")[1];
            			Paint.lstChat.append(usn + ":" +txt + "\r\n");          		
            		
            	}
            	
            	else if(type.equals("list")) {//得到的用户名删掉
            		DefaultListModel<String>  qlm=new DefaultListModel<String>();
                  
                  StringTokenizer st=new StringTokenizer(para,"[,]");
                  while(st.hasMoreTokens())
                  {
                     qlm.addElement(st.nextToken().trim());
                  }
                  Paint.lstUsers.setModel(qlm);
                  if (!(qlm.contains(Paint.username))) { //如果收到的list里没我的名字，那么我就自己退出
                	  System.exit(0);
                  }
                  
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
