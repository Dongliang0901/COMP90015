package client;

import client.CreateCanvas;
import java.util.Scanner;

import java.util.Base64.Encoder;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Base64.Decoder;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import client.ClientBean;
import client.Client;
import client.Read;
import client.Paint.DrawType;

import org.json.JSONException;
import org.json.JSONObject;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BrokenBarrierException;

public class Paint extends JFrame{
    private static BufferedImage image = new BufferedImage(1024,768,BufferedImage.TYPE_INT_BGR);

    public static Graphics2D graphics2D= (Graphics2D) image.getGraphics();
    
    private int fill = 0;

    private int currentMouseX;
    private int currentMouseY;
    private int newMouseX;
    private int newMouseY;
    private int lineX1;
    private int lineY1;
    private int lineX2;
    private int lineY2;
    private int rectX1;
    private int rectY1;
    private int rectX2;
    private int rectY2;
    private int circX1;
    private int circY1;
    private int circX2;
    private int circY2;
    private int circRadius;
    private int ovalX1;
    private int ovalY1;
    private int ovalX2;
    private int ovalY2;

    private JMenuBar menu;
    private JMenuItem freeDraw;
    private JMenu tool;
    private JMenu draw;
    private JMenu shape;
    private JMenu fillShape;

    private JMenuItem becomeManager;
    private JMenuItem becomeClient;
    
    private JMenu thickness;
    private JMenuItem thickness1;
    private static int thick1 = 1;
    private JMenuItem thickness2;
    private static int thick2 = 3;
    private JMenuItem thickness3;
    private static int thick3 = 5;
    private JMenuItem thickness4;
    private static int thick4 = 7;
    private JMenuItem thickness5;
    private static int thick5 = 9;
    private JMenuItem erase;
    private JMenuItem clean;
    
    private JMenuItem line;
    private JMenuItem rectangle;
    private JMenuItem circle;
    private JMenuItem oval;
    private JMenuItem text;
    
    private JMenuItem DoFill;
    private JMenuItem DontFill;

    private JMenuItem Sync;
    
    private JMenu file;
    private JMenuItem save;
    private JMenuItem load;
    private JMenuItem quickLoad;
    private JMenuItem close;

    private JMenuItem foreColorSet;

    static JPanel panel;
	static JPanel disPanel;
	static JTextArea lstChat = new JTextArea();
	static JScrollPane srpList;
    static JList<String> lstUsers;
    JButton send;
    JButton kick;
    private DefaultListModel<String> lm;
    JTextField txtMessage = new JTextField();
    
    private static JLabel name = new JLabel("", JLabel.LEFT);
    private JLabel solid = new JLabel("Hollow", JLabel.LEFT);
    private JLabel type = new JLabel("Free Draw",JLabel.LEFT);
    private JLabel empty = new JLabel("        ",JLabel.LEFT);
    private JLabel Th = new JLabel("Thickness: 1",JLabel.LEFT);
    private JLabel Tf = new JLabel("Input Text: ",JLabel.LEFT);
    private JTextField textField = new JTextField(8);
    
    private static JLabel status = new JLabel("No connection", JLabel.LEFT);
    private String connectStatus = "NA";

    public static String username;
    static String serverAddress;
	static int serverPort;
	static Socket socket = null;

	static Client t;
    static Read r;
    
    static String result;
    
    static int isManager = 0;
    static int isConnect = 0;
    
    static String connectIp;
    
    public static int countClient;

    public static CreateCanvas canvas = new CreateCanvas();
    private static Color forecolor = Color.BLACK;
    private static String colorString = Color2String(forecolor);
    //private static String HexRed = Integer.toHexString(forecolor.getRed());
    //private static String HexBlue = Integer.toHexString(forecolor.getBlue());
    //private static String HexGreen = Integer.toHexString(forecolor.getGreen());
    private static Color backcolor = Color.WHITE;
    private static Color currentColor = forecolor;

    static DrawType drawType = DrawType.PEN;
    static DrawType oldType = DrawType.PEN;
    
    static String shapeType = "";
    
    static int currentThick = thick1;
    static int oldThick = 1;
    static JSONObject json = new JSONObject();
    
    public String getName(){
    	return username;
    }
    public void setName(String newName) {
        this.username = newName;
      }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	serverAddress = "127.0.0.1";
		serverPort = 9999;
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {    	
        		try{
        			Paint paint = new Paint();
        			client();
        			//t.SetSocket(socket);
        		}catch (Exception e) {
					e.printStackTrace();
				}
            	//Paint paint = new Paint();
            }
        });
    }
    
    public static void syncBoard() {
    	if(isManager == 1) {
    		Thread print = new Thread(t);
            Thread read = new Thread(r);
            t.SetMsg("SYNC" + "&" + getImageStr(image), socket);
            print.start();
            read.start();
    	}else {
    		JOptionPane.showMessageDialog(null, "Oops! You are not the manager! Only manager can synchronize all white boards!");
    	}
    	
    }
    
    public static void acceptConnection(String client) {
    	Thread print = new Thread(t);
        Thread read = new Thread(r);
        int n =JOptionPane.showConfirmDialog(null, "The client "+ client +" is asking for connect","Connection request",JOptionPane.YES_NO_OPTION );
    	t.SetMsg("ReplyRequest"+"&"+client+"&"+n, socket);
    	print.start();
        read.start();
    }
    
    public static void connectionSuccess(int n) {
    	if(n == 0) {
    		isConnect = 1;
    		//cleanBoard(graphics2D);
    		addToList();
    		resetStatusShow(status);
    		Thread print = new Thread(t);
            Thread read = new Thread(r);
    		t.SetMsg("RequestImage"+"&"+countClient+"&"+username, socket);
    		System.out.println("Requesting image");
        	print.start();
            read.start();
    	}else if(n == 1) {
    		isConnect = 0;
    	}
    	
    }
    
    public static void sendImageToClient(String client) {
    	Thread print = new Thread(t);
        Thread read = new Thread(r);
		t.SetMsg("SendImage"+"&"+client+"&"+ getImageStr(image), socket);
		System.out.println("Image Send to Client");
    	print.start();
        read.start();
    }
    
    public static void receiveImageAndShow(String img) {
    	System.out.println("The image is: "+img);
    	Image image = generateImage(img);
    	System.out.println("Image Received");
    	graphics2D.drawImage(image, 0, 0, null);
    }
    
    /***************ColorConverter*********************/
    public static Color String2Color(String str) {
		int i =   Integer.parseInt(str.substring(1),16);
		return new Color(i);
	}
	
	public static String Color2String(Color color) {
		String R = Integer.toHexString(color.getRed());
		R = R.length()<2?('0'+R):R;
		String B = Integer.toHexString(color.getBlue());
		B = B.length()<2?('0'+B):B;
		String G = Integer.toHexString(color.getGreen());
		G = G.length()<2?('0'+G):G;
		return '#'+R+B+G;
	}
	/******************ColorConverter*********************/
    
    //Start connection to the server
  	public static void client() throws InterruptedException, BrokenBarrierException {
  		countClient = (int)(1+Math.random()*10000);
  		DefaultListModel<String> lm=new DefaultListModel<String>();
      	System.out.println("************CLient"+countClient +"*************");
          try {
              socket = new Socket(serverAddress, serverPort);
              username = JOptionPane.showInputDialog(null,"Enter your username",JOptionPane.PLAIN_MESSAGE);
              name.setText("USER: "+username);
              Paint.lstUsers.setModel(lm);
              if(lm.contains(username)){
              username = JOptionPane.showInputDialog(null,"Name already exists！Enter your username",JOptionPane.PLAIN_MESSAGE);
          }  
          
              System.out.println("input username:");
//              Scanner o = new Scanner(System.in);
//			String name = o.next();
//			username = name;
			PrintWriter out = new PrintWriter(socket.getOutputStream());
			//out.println("newuser;" + username);
			//out.flush();
          } catch (Exception e) {
          	System.out.println("Oops... connection failed!");
              e.printStackTrace();
          }
          t = new Client(socket,username);
          r = new Read(socket, username);
          Thread print = new Thread(t);
          Thread read = new Thread(r);
          print.start();
          read.start();
          
      }
    
    //Draw Type
    public enum DrawType {
    	PEN, ERASER, TEXT, RECT, CIRC, OVAL, LINE
    }



    public Paint(){
        super();
        setTitle("Paint Board");
        setBounds(0,0,1024,768);
        setResizable(true);

        init();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        //t.SetSocket(socket);
    }
    
    /******************remoteDraw*********************/
    /*
    public static void remotePEN(int x0, String color, int x1, int y1, int x2, int y2) {
    	Color newColor = String2Color(color);
    	graphics2D.setColor(newColor);
    	graphics2D.setStroke(new BasicStroke(x0));
    	graphics2D.drawLine(x1, y1, x2, y2);
		System.out.println("Thickness changed");
		canvas.repaint();
		graphics2D.setStroke(new BasicStroke(currentThick));
		graphics2D.setColor(forecolor);
    }
    */
    
    public static void remoteDraw(String type, int thickness,String color, int fill, int x1, int y1, int x2, int y2) {
    	Color newColor = String2Color(color);
    	graphics2D.setColor(newColor);
    	graphics2D.setStroke(new BasicStroke(thickness));
    	//graphics2D.setColor(color);
    	if(type.equals("RECT")) {
    		if(fill == 0) {
    			graphics2D.drawRect(x1, y1, x2, y2);
    		}else if(fill == 1) {
    			graphics2D.fillRect(x1, y1, x2, y2);
    		}
    		
    		//System.out.println("Rectangle draw");
    	}else if(type.equals("PEN")) {
    		graphics2D.drawLine(x1, y1, x2, y2);
    		//System.out.println("Free draw");
    	}else if(type.equals("CIRC")) {
    		if(fill == 0) {
    			graphics2D.drawArc(x1, y1, x2, y2, 0, 360);
    		}else if(fill == 1) {
    			graphics2D.fillArc(x1, y1, x2, y2, 0, 360);
    		}
    		
    		//System.out.println("Cricle draw");
    	}else if(type.equals("OVAL")) {
    		if(fill == 0) {
    			graphics2D.drawOval(x1, y1, x2, y2);
    		}else if(fill == 1) {
    			graphics2D.fillOval(x1, y1, x2, y2);
    		}
    		
    		//System.out.println("Oval draw");
    	}else if(type.equals("LINE")) {
    		graphics2D.drawLine(x1, y1, x2, y2);
    		//System.out.println("Line draw");
    	}else if(type.equals("ERASER")){
    		graphics2D.setColor(backcolor);
    		graphics2D.drawLine(x1, y1, x2, y2);
    		graphics2D.setColor(forecolor);
    	}
    	canvas.repaint();
    	drawType = oldType;
    	graphics2D.setStroke(new BasicStroke(currentThick));
    	graphics2D.setColor(forecolor);
    }
    
    public static void remoteText(String color, String text, int x, int y) {
    	Color newColor = String2Color(color);
    	graphics2D.setColor(newColor);
    	graphics2D.drawString(text, x, y);
		System.out.println("Text draw");
		canvas.repaint();
    	drawType = oldType;  
    	graphics2D.setColor(forecolor);
    }
    
    /*******************shapes generator********************************/
    
    public void generateRect(Graphics2D graphics2D, int x0, int x1, int x2, int x3) {
    	if(fill == 0) {
    		graphics2D.drawRect(x0, x1, x2, x3);
    	}else if(fill == 1) {
    		graphics2D.fillRect(x0, x1, x2, x3);
    	}
    }
    
    public void generateCircle(Graphics2D graphics2D, int x0, int x1, int x2, int x3) {
    	if(fill == 0) {
    		graphics2D.drawArc(x0, x1, x2, x3, 0, 360);
    	}else if(fill == 1) {
    		graphics2D.fillArc(x0, x1, x2, x3, 0, 360);
    	}
    }
    
    public void generateOval(Graphics2D graphics2D, int x0, int x1, int x2, int x3) {
    	if(fill == 0) {
    		graphics2D.drawOval(x0, x1, x2, x3);
    	}else if(fill == 1) {
    		graphics2D.fillOval(x0, x1, x2, x3);
    	}
    }
    
    /*******************shapes generator********************************/
    public static void resetStatusShow(JLabel status) {
    	if(isConnect == 0 && isManager == 0) {
    		status.setText("No Connection");
    	}else if(isManager == 1) {
    		status.setText("Connected as Manager");
    	}else if(isConnect == 1) {
    		status.setText("Connected as Client");
    	}
    }
    /*************************************************/

    public void init(){

        panel = new JPanel(new FlowLayout());
		panel.setPreferredSize(new Dimension(200, 100));
		panel.setSize(200, 768);
		
		lstUsers = new JList<String>();
		srpList = new JScrollPane(lstUsers);
		srpList.setPreferredSize(new Dimension(199, 99));
		lstUsers.setPreferredSize(new Dimension(199, 99));

		lstUsers.setFont(new java.awt.Font("Dialog", Font.PLAIN, 12));
//		lstUsers.setModel(new DefaultListModel());
		lstUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// getContentPane().setLayout(null);

		lstChat = new JTextArea();
		lstChat.setPreferredSize(new Dimension(199, 400));

		txtMessage = new JTextField();
		txtMessage.setPreferredSize(new Dimension(199, 99));

		send = new JButton("send");
		kick = new JButton("kick");
		panel.add(srpList, BorderLayout.EAST);
		panel.add(kick, BorderLayout.AFTER_LINE_ENDS);
		panel.add(lstChat, BorderLayout.EAST);
		panel.add(txtMessage, BorderLayout.EAST);
		panel.add(send, BorderLayout.AFTER_LINE_ENDS);

		getContentPane().add(panel, BorderLayout.EAST);

		// Display Panel
		
		disPanel = new JPanel(new FlowLayout());
		disPanel.setPreferredSize(new Dimension(200, 29));
		disPanel.setSize(568, 200);
		
		disPanel.add(name);
		disPanel.add(empty);
		disPanel.add(status);
		disPanel.add(empty);
		disPanel.add(solid);
		disPanel.add(empty);
		disPanel.add(type);
		disPanel.add(empty);
		disPanel.add(Th);
		disPanel.add(empty);
		disPanel.add(Tf);
		disPanel.add(textField);

		getContentPane().add(disPanel, BorderLayout.SOUTH);
        /*************************************Menu*******************************************/
        menu = new JMenuBar();
        // File
        file = new JMenu("File");
        save = new JMenuItem("Save");
        load = new JMenuItem("Load");
        quickLoad = new JMenuItem("Quick Load");
        close = new JMenuItem("Close");
        file.add(save);
        file.add(load);
        file.add(close);
        //file.add(quickLoad);
        
        menu.add(file);
        
        // Tool
        tool = new JMenu("Tool");        
        thickness = new JMenu("Thickness");
        thickness1 = new JMenuItem("1");
        thickness2 = new JMenuItem("2");
        thickness3 = new JMenuItem("3");
        thickness4 = new JMenuItem("4");
        thickness5 = new JMenuItem("5");
        thickness.add(thickness1);
        thickness.add(thickness2);
        thickness.add(thickness3);
        thickness.add(thickness4);
        thickness.add(thickness5);
        tool.add(thickness);
        erase = new JMenuItem("Eraser");
        tool.add(erase);
        clean = new JMenuItem("Clean");
        tool.add(clean);
        foreColorSet = new JMenuItem("Set Color");
        tool.add(foreColorSet);
        
        becomeManager = new JMenuItem("Become Manager");
        tool.add(becomeManager);
        
        becomeClient = new JMenuItem("Connect to a Manager");
        tool.add(becomeClient);
        
        Sync = new JMenuItem("Synchronize");
        tool.add(Sync);
        
        menu.add(tool);
        /**********************************************************/
           
        //Draw
        draw = new JMenu("Draw");
        freeDraw = new JMenuItem("Free Draw");
        draw.add(freeDraw);
        text = new JMenuItem("Text");
        draw.add(text);
        
        shape = new JMenu("Shape");
        line = new JMenuItem("Line");
        shape.add(line);
        rectangle = new JMenuItem("Rectangle");
        shape.add(rectangle);
        circle = new JMenuItem("Circle");
        shape.add(circle);
        oval = new JMenuItem("Oval");
        shape.add(oval);
        draw.add(shape);
        
        fillShape = new JMenu("Solid/Hollow shape");
        DoFill = new JMenuItem("Solid");
        fillShape.add(DoFill);
        DontFill = new JMenuItem("Hollow");
        fillShape.add(DontFill);
        draw.add(fillShape);
        
        menu.add(draw);
        
        

        setJMenuBar(menu);
        /*************************************Menu*******************************************/



        // Set color
        graphics2D.setColor(backcolor);
        graphics2D.fillRect(0, 0, 1024, 768);
        graphics2D.setColor(forecolor);
        canvas.setBackground(backcolor);
        canvas.setImage(image);
        canvas.paint(graphics2D);
        getContentPane().add(canvas, BorderLayout.CENTER);
        addListener();
        
        
        // Show status
        //type.setLocation(505, 100);
        

    }
    
    public static void addToList() {
    	//System.out.println("Manager added");
    	PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream());
			out.println("newuser;" + username);
			out.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    /********************reset bottom line**********************************/
    
    public void resetThickness(JLabel Th) {
    	
    }
    
    public void resetSolidShow(JLabel solid) {
    	if(fill == 1) {
    		solid.setText("Solid");
    	}else if(fill == 0) {
    		solid.setText("Hollow");
    	}
    }
    
    public void resetTypeShow(JLabel type) {
    	if(drawType == DrawType.PEN) {
    		type.setText("Free Draw");
        }else if(drawType == DrawType.ERASER) {
        	type.setText("Eraser");
        }else if(drawType == DrawType.TEXT) {
        	type.setText("Text");
        }else if(drawType == DrawType.RECT) {
        	type.setText("Rectangle");
        }else if(drawType == DrawType.CIRC) {
        	type.setText("Circle");
        }else if(drawType == DrawType.OVAL) {
        	type.setText("Oval");
        }else if(drawType == DrawType.LINE) {
        	type.setText("Line");
        }
    }

    /********************reset bottom line**********************************/
    
    public void getNewCoordinate(MouseEvent e) {
    	newMouseX = e.getX();
    	newMouseY = e.getY();
    }
    
    public void resetCoordinate(MouseEvent e) {
    	currentMouseX = e.getX();
    	currentMouseY = e.getY();
    }
    
    public static void cleanBoard(Graphics2D g) {
    	g.setColor(Color.WHITE);
        g.fillRect(0, 0, 1024, 768);
        g.setColor(forecolor);
        canvas.repaint();
    }

    public void addListener(){
        //preparing
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                currentMouseX = e.getX();
                currentMouseY = e.getY();
                //System.out.println("coordinate: ("+ currentMouseX + "," + currentMouseY + ")");
                if(drawType == DrawType.RECT) {
                	rectX1 = e.getX();
                	rectY1 = e.getY();
                }else if(drawType == DrawType.CIRC) {
                	circX1 = e.getX();
                	circY1 = e.getY();
                }else if(drawType == DrawType.OVAL) {
                	ovalX1 = e.getX();
                	ovalY1 = e.getY();
                }else if(drawType == DrawType.LINE) {
                	lineX1 = e.getX();
                	lineY1 = e.getY();
                	
                }
            }
            
            


        });
        //text filed
        
        canvas.addMouseListener(new MouseAdapter() {
        	@Override
            public void mouseReleased(MouseEvent e) {
        		//System.out.println("Mouse Released activated");
        		Thread print = new Thread(t);
				Thread read = new Thread(r);
        		// Draw rectangle
            	if(drawType == DrawType.RECT) {
            		shapeType = "RECT";
            		rectX2 = e.getX();
            		rectY2 = e.getY();
            		graphics2D.setColor(forecolor);
            		if(rectX2 >= rectX1 && rectY2 >= rectY1) {
            			//graphics2D.drawRect(rectX1, rectY1, rectX2-rectX1, rectY2-rectY1);
            			generateRect(graphics2D, rectX1, rectY1, rectX2-rectX1, rectY2-rectY1);
            			try {
            				json.put("Type", "RECT");
							json.put("thick", currentThick);
							json.put("color", colorString);
	            			json.put("fill", fill);
	            			json.put("pos1", rectX1);
	            			json.put("pos2", rectY1);
	            			json.put("pos3", rectX2 - rectX1);
	            			json.put("pos4", rectY2 - rectY1);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket);
//            			t.SetMsg(currentThick + "&" +colorString + "&" + fill + "&" +rectX1 + "&" + rectY1 + "&" + (rectX2 - rectX1) + "&" + (rectY2 - rectY1), socket);         			
            		}else if(rectX2 >= rectX1 && rectY2 <= rectY1) {
            			//graphics2D.drawRect(rectX1, rectY2, rectX2-rectX1, rectY1-rectY2);
            			generateRect(graphics2D, rectX1, rectY2, rectX2-rectX1, rectY1-rectY2);
            			try {
            				json.put("Type", "RECT");
							json.put("thick", currentThick);
							json.put("color", colorString);
	            			json.put("fill", fill);
	            			json.put("pos1", rectX1);
	            			json.put("pos2", rectY2);
	            			json.put("pos3", rectX2 - rectX1);
	            			json.put("pos4", rectY1 - rectY2);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket); 
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +rectX1 + "&" + rectY2 + "&" + (rectX2 - rectX1) + "&" + (rectY1 - rectY2), socket);
            		}else if(rectX2 <= rectX1 && rectY2 >= rectY1) {
            			//graphics2D.drawRect(rectX2, rectY1, rectX1-rectX2, rectY2-rectY1);
            			generateRect(graphics2D, rectX2, rectY1, rectX1-rectX2, rectY2-rectY1);
            			try {
            				json.put("Type", "RECT");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", rectX2);
	            			json.put("pos2", rectY1);
	            			json.put("pos3", (rectX1 - rectX2));
	            			json.put("pos4", (rectY2 - rectY1));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket);
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +rectX2 + "&" + rectY1 + "&" + (rectX1 - rectX2) + "&" + (rectY2 - rectY1), socket);
            		}else if(rectX2 <= rectX1 && rectY2 <= rectY1) {
            			//graphics2D.drawRect(rectX2, rectY2, rectX1-rectX2, rectY1-rectY2);
            			generateRect(graphics2D, rectX2, rectY2, rectX1-rectX2, rectY1-rectY2);
            			try {
            				json.put("Type", "RECT");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("para1", rectX2);
	            			json.put("para2", rectY2);
	            			json.put("para3", (rectX1 - rectX2));
	            			json.put("para4", (rectY1 - rectY2));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket);
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +rectX2 + "&" + rectY2 + "&" + (rectX1 - rectX2) + "&" + (rectY1 - rectY2), socket);
            		}           		
            	// Draw Circle
            	}else if(drawType == DrawType.CIRC) {
            		shapeType = "CIRC";
            		circX2 = e.getX();
            		circY2 = e.getY();
            		graphics2D.setColor(forecolor);
            		int absX = Math.abs(circX2 - circX1);
            		int absY = Math.abs(circY2 - circY1);
            		circRadius = (int) Math.sqrt(absX*absX + absY*absY);
            		if(circX2 >= circX1 && circY2 >= circY1) {    			
                		//graphics2D.drawArc(circX1, circY1, circRadius, circRadius, 0, 360);
                		generateCircle(graphics2D, circX1, circY1, circRadius, circRadius);
                		try {
                			json.put("Type", "CIRC");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", circX1);
	            			json.put("pos2", circY1);
	            			json.put("pos3", circRadius);
	            			json.put("pos4", circRadius);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                		t.SetMsg("json&" + json.toString(), socket);
//                		t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +circX1 + "&" + circY1 + "&" + circRadius + "&" + circRadius, socket); 
            		}else if(circX2 <= circX1 && circY2 >= circY1) {
                		//graphics2D.drawArc(circX2, circY1, circRadius, circRadius, 0, 360);
                		generateCircle(graphics2D, circX2, circY1, circRadius, circRadius);
                		try {
                			json.put("Type", "CIRC");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", circX2);
	            			json.put("pos2", circY1);
	            			json.put("pos3", circRadius);
	            			json.put("pos4", circRadius);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                		t.SetMsg("json&" + json.toString(), socket);
//                		t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +circX2 + "&" + circY1 + "&" + circRadius + "&" + circRadius, socket); 
            		}else if(circX2 >= circX1 && circY2 <= circY1) {
            			//graphics2D.drawArc(circX1, circY2, circRadius, circRadius, 0, 360);
            			generateCircle(graphics2D, circX1, circY2, circRadius, circRadius);
            			try {
            				json.put("Type", "CIRC");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", circX1);
	            			json.put("pos2", circY2);
	            			json.put("pos3", circRadius);
	            			json.put("pos4", circRadius);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket); 
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +circX1 + "&" + circY2 + "&" + circRadius + "&" + circRadius, socket); 
            		}else if(circX1 >= circX2 && circY1 >= circY2) {
            			//graphics2D.drawArc(circX2, circY2, circRadius, circRadius, 0, 360);
            			generateCircle(graphics2D, circX2, circY2, circRadius, circRadius);
            			try {
            				json.put("Type", "CIRC");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", circX2);
	            			json.put("pos2", circY2);
	            			json.put("pos3", circRadius);
	            			json.put("pos4", circRadius);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket); 
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +circX2 + "&" + circY2 + "&" + circRadius + "&" + circRadius, socket); 
            		}
            	// Draw Oval
            	}else if(drawType == DrawType.OVAL) {
            		shapeType = "OVAL";
            		ovalX2 = e.getX();
            		ovalY2 = e.getY();
            		graphics2D.setColor(forecolor);
            		if(ovalX2 >= ovalX1 && ovalY2 >= ovalY1) {
            			//graphics2D.drawOval(ovalX1, ovalY1, ovalX2-ovalX1, ovalY2-ovalY1);
            			generateOval(graphics2D, ovalX1, ovalY1, ovalX2-ovalX1, ovalY2-ovalY1);
            			try {
            				json.put("Type", "OVAL");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", ovalX1);
	            			json.put("pos2", ovalY1);
	            			json.put("pos3", (ovalX2-ovalX1));
	            			json.put("pos4", (ovalY2-ovalY1));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket); 
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill+ "&" +ovalX1 + "&" + ovalY1 + "&" + (ovalX2-ovalX1) + "&" + (ovalY2-ovalY1), socket); 
            		}else if(ovalX2 >= ovalX1 && ovalY2 <= ovalY1) {
            			//graphics2D.drawOval(ovalX1, ovalY2, ovalX2-ovalX1, ovalY1-ovalY2);
            			generateOval(graphics2D, ovalX1, ovalY2, ovalX2-ovalX1, ovalY1-ovalY2);
            			try {
            				json.put("Type", "OVAL");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", ovalX1);
	            			json.put("pos2", ovalY2);
	            			json.put("pos3", (ovalX2-ovalX1));
	            			json.put("pos4", (ovalY1-ovalY2));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket); 
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill+ "&" +ovalX1 + "&" + ovalY2 + "&" + (ovalX2-ovalX1) + "&" + (ovalY1-ovalY2), socket); 
            		}else if(ovalX2 <= ovalX1 && ovalY2 >= ovalY1) {
            			//graphics2D.drawOval(ovalX2, ovalY1, ovalX1-ovalX2, ovalY2-ovalY1);
            			generateOval(graphics2D, ovalX2, ovalY1, ovalX1-ovalX2, ovalY2-ovalY1);
            			try {
            				json.put("Type", "OVAL");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", ovalX2);
	            			json.put("pos2", ovalY1);
	            			json.put("pos3", (ovalX1-ovalX2));
	            			json.put("pos4", (ovalY2-ovalY1));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket);
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill+ "&" +ovalX2 + "&" + ovalY1 + "&" + (ovalX1-ovalX2) + "&" + (ovalY2-ovalY1), socket); 
            		}else if(ovalX2 <= ovalX1 && ovalY2 <= ovalY1) {
            			//graphics2D.drawOval(ovalX2, ovalY2, ovalX1-ovalX2, ovalY1-ovalY2);
            			generateOval(graphics2D, ovalX2, ovalY2, ovalX1-ovalX2, ovalY1-ovalY2);
            			try {
            				json.put("Type", "OVAL");
							json.put("thick", currentThick);
							json.put("color", colorString);
							json.put("fill", fill);
	            			json.put("pos1", ovalX2);
	            			json.put("pos2", ovalY2);
	            			json.put("pos3", (ovalX1-ovalX2));
	            			json.put("pos4", (ovalY1-ovalY2));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
            			t.SetMsg("json&" + json.toString(), socket);
//            			t.SetMsg(currentThick + "&" + colorString + "&" + fill+ "&" +ovalX2 + "&" + ovalY2 + "&" + (ovalX1-ovalX2) + "&" + (ovalY1-ovalY2), socket); 
            		}
            	}else if(drawType == DrawType.TEXT) {
            		shapeType = "TEXT";
            		graphics2D.drawString(textField.getText(), currentMouseX, currentMouseY);
            		t.SetMsg(colorString + "&" +textField.getText() + "&" + currentMouseX + "&" + currentMouseY + "&", socket);
            	}else if(drawType == DrawType.LINE) {
            		shapeType = "LINE";
            		lineX2 = e.getX();
            		lineY2 = e.getY();
            		graphics2D.drawLine(lineX1, lineY1, lineX2, lineY2);
            		try {
            			json.put("Type", "LINE");
						json.put("thick", currentThick);
						json.put("color", colorString);
            			json.put("pos1", lineX1);
            			json.put("pos2", lineY1);
            			json.put("pos3", lineX2);
            			json.put("pos4", lineY2);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
            		t.SetMsg("json&" + json.toString(), socket); 
//            		t.SetMsg(currentThick + "&" + colorString + "&" + fill + "&" +lineX1 + "&" + lineY1 + "&" + lineX2 + "&" + lineY2, socket); 
            	}
            	print.start();
				read.start();
        		canvas.repaint();
            }
        });
        

        //brush listener
        canvas.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e){
            	Thread print = new Thread(t);
				Thread read = new Thread(r);
                if(drawType == DrawType.PEN){
                	shapeType = "PEN";
                	getNewCoordinate(e);
                    graphics2D.setColor(forecolor);
                    graphics2D.drawLine(currentMouseX,currentMouseY,newMouseX,newMouseY);
                    try {
            			json.put("Type", "PEN");
						json.put("thick", currentThick);
						json.put("color", colorString);
						json.put("fill", fill);
            			json.put("pos1", currentMouseX);
            			json.put("pos2", currentMouseY);
            			json.put("pos3", newMouseX);
            			json.put("pos4", newMouseY);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    t.SetMsg("json&" + json.toString(), socket); 
//                    t.SetMsg(currentThick + "&" + colorString + "&" + fill+ "&" + currentMouseX + "&" + currentMouseY + "&" + newMouseX + "&" + newMouseY, socket); 
                    print.start();
                    read.start();
                }
               // if(drawType == DrawType.RECT) {
                	
               // }
                if(drawType == DrawType.ERASER){
                	shapeType = "ERASER";
                	getNewCoordinate(e);
                    graphics2D.setColor(backcolor);
                    graphics2D.drawLine(currentMouseX,currentMouseY,newMouseX,newMouseY);
                    try {
            			json.put("Type", "ERASER");
						json.put("thick", currentThick);
						json.put("color", colorString);
            			json.put("pos1", currentMouseX);
            			json.put("pos2", currentMouseY);
            			json.put("pos3", newMouseX);
            			json.put("pos4", newMouseY);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    t.SetMsg("json&" + json.toString(), socket); 
//                    t.SetMsg(currentThick + "&" + colorString + "&" +currentMouseX + "&" + currentMouseY + "&" + newMouseX + "&" + newMouseY, socket); 
                    print.start();
                    read.start();
                }
                resetCoordinate(e);
                canvas.repaint();

            }
        });

        
        freeDraw.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.PEN;
				oldType = DrawType.PEN;
            	resetTypeShow(type);
			}
        	
        });
        
        erase.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.ERASER;
				oldType = DrawType.ERASER;
            	resetTypeShow(type);
			}
        	
        });
        
        clean.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(isManager == 1) {
					cleanBoard(graphics2D);
					Thread print = new Thread(t);
					Thread read = new Thread(r);		
					t.SetMsg("clean", socket);
					print.start();
					read.start();
				}else {
					JOptionPane.showMessageDialog(null, "Oops! You are not the manager! Only manager can clean the white board!");
				}
				
			}
        	
        });
        
        line.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.LINE;
				oldType = DrawType.LINE;
            	resetTypeShow(type);
			}
        	
        });
        
        rectangle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.RECT;
				oldType = DrawType.RECT;
            	resetTypeShow(type);
			}
        	
        });
        
        circle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.CIRC;
				oldType = DrawType.CIRC;
            	resetTypeShow(type);
			}
        	
        });
        
        oval.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.OVAL;
				oldType = DrawType.OVAL;
            	resetTypeShow(type);
			}
        	
        });
        
        text.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				drawType = DrawType.TEXT;
				oldType = DrawType.TEXT;
				resetTypeShow(type);
			}
        	
        });
        
        DoFill.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fill = 1;
				resetSolidShow(solid);
			}
        	
        });
        
        DontFill.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				fill = 0;
				resetSolidShow(solid);
			}
        	
        });
        
        becomeManager.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
                isManager = 1;
                shapeType = "user";
                addToList();
				JOptionPane.showMessageDialog(null, "You have become the manager!" );
				resetStatusShow(status);
			}
        	
        });
        
        becomeClient.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Thread print = new Thread(t);
				Thread read = new Thread(r);
				//isConnect = 1;
				connectIp = JOptionPane.showInputDialog(null,"Enter the IP address of the Manager you want to connect：\n","Connect to a Manager",JOptionPane.PLAIN_MESSAGE);
				t.SetMsg("AskForConnect" + "&" + countClient, socket);
				print.start();
				read.start();
			}
        	
        });
        

      //Thickness 1
        thickness1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphics2D.setStroke(new BasicStroke(thick1));
                currentThick = thick1;
                Th.setText("Thickness: 1");
            }
        });
        
      //Thickness 2
        thickness2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphics2D.setStroke(new BasicStroke(thick2));
                currentThick = thick2;
                Th.setText("Thickness: 2");
            }
        });
        
      //Thickness 3
        thickness3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphics2D.setStroke(new BasicStroke(thick3));
                currentThick = thick3;
                Th.setText("Thickness: 3");
            }
        });
        
      //Thickness 4
        thickness4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphics2D.setStroke(new BasicStroke(thick4));
                currentThick = thick4;
                Th.setText("Thickness: 4");
            }
        });
        
      //Thickness 5
        thickness5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphics2D.setStroke(new BasicStroke(thick5));
                currentThick = thick5;
                Th.setText("Thickness: 5");
            }
        });
        
        Sync.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				syncBoard();
			}
        	
        });


        //color set
        foreColorSet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color frColor = JColorChooser.showDialog(Paint.this, "Fore Color", Color.BLACK);
                if (!(frColor == null)) {
                    forecolor = frColor;
                    colorString = Color2String(forecolor);
                }
                graphics2D.setColor(forecolor);
                canvas.repaint();
            }
        });


        //file propose
        save.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO Auto-generated method stub
            	if(isManager == 1) {
            		JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter(".png", "png");
                    chooser.setFileFilter(jpgFilter);
                    int returnVal = chooser.showSaveDialog(Paint.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        String selectPath = chooser.getSelectedFile().getPath() + ".png";
                        try {
                            ImageIO.write(image, "png", new File(selectPath));
                            File f = new File(selectPath);
                            if (f.exists()) {
                                JOptionPane.showMessageDialog(Paint.this, "save successfully", "Success",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
            	}else {
            		JOptionPane.showMessageDialog(null, "Oops! You are not the manager! Only manager can save an image!");
            	}
                
            }
        });
        load.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
                // TODO Auto-generated method stub
            	if(isManager == 1) {
            		Thread print = new Thread(t);
    				Thread read = new Thread(r);
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter(".png", "png");
                    chooser.setFileFilter( jpgFilter);
                    int returnVal = chooser.showOpenDialog(Paint.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File path = chooser.getSelectedFile();
                        Image tempImage = null;
                        try {
                            tempImage = ImageIO.read(path);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        tempImage = tempImage.getScaledInstance(824,768,Image.SCALE_SMOOTH);
                        t.SetMsg("image" + "&" +getImageStr(tempImage), socket);
                        graphics2D.drawImage(tempImage,0,0,null);
                        canvas.repaint();
                        System.out.println(path);
                    }
                    print.start();
                    read.start();
            	}else {
            		JOptionPane.showMessageDialog(null, "Oops! You are not the manager! Only manager can open an image!");
            	}
            	
            }
        });
        
        close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(isManager == 1) {
					Thread print = new Thread(t);
					Thread read = new Thread(r);
					t.SetMsg("close", socket);
					print.start();
                    read.start();
                    System.exit(0);
				}else {
					JOptionPane.showMessageDialog(null, "Oops! You are not the manager! Only manager can close the white board!");
				}
				
				
			}
        	
        });
        send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Thread print = new Thread(t);
				Thread read = new Thread(r);
				String text = txtMessage.getText().trim();
				// System.out.println(text);
				// txtMessage.setText("");
				if (text.equals("")) {
					JOptionPane.showMessageDialog(null, "Cannot be empty");
				} else {
					shapeType = "chat";
					t.SetMsg(username + "&" + text, socket);
					// System.out.println(te xt);
//username鏄垜鐧诲綍鏃跺�欒緭鍏ュ彂缁檚erver鐨剈sername
				}
				print.start();
				read.start();
			}
		});

		kick.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(isManager == 1) {
					Thread print = new Thread(t);
					Thread read = new Thread(r);
					String username = lstUsers.getSelectedValue();
//					System.out.println("obj IS"+obj);
					if (username != null) {
//						ClientBean cb = (ClientBean) obj;
						
//						((DefaultListModel) lstUsers.getModel()).removeElement(cb);
//						String username = cb.getClientName();
//						try {
//							cb.getS().close();
//						} catch (Exception ex) {
//							ex.printStackTrace();
//						} finally {
							shapeType = "kick";
							t.SetMsg(username, socket);
						
					} else {
						JOptionPane.showMessageDialog(null, "Choose the one u wanna kick");
					}
					print.start();
					read.start();
				}else {
					JOptionPane.showMessageDialog(null, "Ooops! You are not the manager ! Only manager can kick out a person!");
				}
				
				
			}
		});
    }
    /**********************ImageStringConverter********************************/
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
    
    
    public static String getImageStr(Image image) {

    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
    	BufferedImage bimage = toBufferedImage(image);

    	try {
			ImageIO.write(bimage,"png",bos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	//bos.toByteArray();
    	 Encoder encoder = Base64.getEncoder();
    	 return encoder.encodeToString(bos.toByteArray());
    }
    
    public static Image generateImage(String imgStr) {   
    	BufferedImage img = null;
				Decoder decoder = Base64.getDecoder();
				try {
					// 瑙ｅ瘑
					byte[] b = decoder.decode(imgStr);
					// 澶勭悊鏁版嵁
					for(int i = 0; i < b.length; ++i) {
						if (b[i] < 0) {
							b[i] += 256;
						}
					}
					OutputStream out = new FileOutputStream("image.png");
					out.write(b);
					out.flush();
					out.close();
					InputStream buffin = new ByteArrayInputStream(b);
					img = ImageIO.read(buffin);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return img;
				
			}
    /***********************ImageStringConverter*******************************/
}
