import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.awt.Color;

public class ClientView {

	private JFrame frame;
	private JTextField searchWord;
	private JButton btnExit;
	private JTextField deleteWord;
	private JTextField addWord;
	private JButton Add;
	private JButton Delete;
	private JTextArea addMeaning;
	private JTextArea showResponse;
	private static String host;
	private static int port;

	public static void main(String[] args) {
//		host = args[0];
//		port = Integer.parseInt(args[1]);
		host = "localhost";
		port = 1234;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientView window = new ClientView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ClientView() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 534, 847);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		searchWord = new JTextField();
		searchWord.setBounds(31, 19, 230, 45);
		searchWord.setFont(new Font("Arial", Font.PLAIN, 25));
		frame.getContentPane().add(searchWord);
		searchWord.setColumns(10);
		
		JTextArea wordMeaning = new JTextArea();
		wordMeaning.setBounds(31, 97, 448, 150);
		wordMeaning.setEnabled(false);
		wordMeaning.setFont(new Font("Arial", Font.PLAIN, 25));
		wordMeaning.setLineWrap(true);
		wordMeaning.setEditable(false);
		frame.getContentPane().add(wordMeaning);
		
		JButton Search = new JButton("SEARCH");
		Search.setBounds(300, 22, 140, 40);
		Search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String word = searchWord.getText();
				if(word.equals("")){
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				}else{
					String message = TCPC.connect(host, port, "SEARCH", word, null);
					wordMeaning.setText(message);
				}
			}
		});
		Search.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.getContentPane().add(Search);
		
		addMeaning = new JTextArea();
		addMeaning.setBounds(31, 367, 462, 150);
		addMeaning.setLineWrap(true);
		addMeaning.setFont(new Font("Arial", Font.PLAIN, 25));
		frame.getContentPane().add(addMeaning);
		
		btnExit = new JButton("EXIT");
		btnExit.setBounds(187, 728, 135, 31);
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		btnExit.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.getContentPane().add(btnExit);
		
		deleteWord = new JTextField();
		deleteWord.setBounds(31, 556, 230, 45);
		deleteWord.setFont(new Font("Arial", Font.PLAIN, 25));
		deleteWord.setColumns(6);
		frame.getContentPane().add(deleteWord);
		
		addWord = new JTextField();
		addWord.setBounds(31, 290, 230, 45);
		addWord.setFont(new Font("Arial", Font.PLAIN, 25));
		addWord.setColumns(6);
		frame.getContentPane().add(addWord);
		
		showResponse = new JTextArea();
		showResponse.setBounds(24, 639, 462, 60);
		showResponse.setForeground(new Color(0, 0, 0));
		showResponse.setFont(new Font("Arial", Font.PLAIN, 25));
		showResponse.setLineWrap(true);
		showResponse.setEnabled(false);
		showResponse.setEditable(false);
		frame.getContentPane().add(showResponse);
		
		Add = new JButton("ADD");
		Add.setBounds(300, 293, 140, 40);
		Add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = addWord.getText();
				String meaning = addMeaning.getText();
				if(word.equals("")||meaning.equals("")){
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				}else{
					String message = TCPC.connect(host, port, "ADD", word, meaning);
					showResponse.setText(message);
				}
			}
		});
		Add.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.getContentPane().add(Add);
		
		Delete = new JButton("DELETE");
		Delete.setBounds(300, 559, 140, 40);
		Delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = deleteWord.getText();
				if(word.equals("")){
					JOptionPane.showMessageDialog(null, "Invalid Input!");
				}else{
					String message = TCPC.connect(host, port, "DELETE", word, null);
					showResponse.setText(message);
				}
			}
		});
		Delete.setFont(new Font("Arial", Font.PLAIN, 20));
		frame.getContentPane().add(Delete);
		
		
		
		
		
		
	}
}
