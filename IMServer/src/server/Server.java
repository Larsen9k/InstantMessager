package server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	//constructor
	public Server() {
		super("Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userText.setText("");
				}
			}
		);
		
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(800,600);
		setVisible(true);
	}
	
	//set up and run the server
	public void startServer() {
		try {
			server = new ServerSocket(6789, 10);//6789
			while(true) {
				try {
					waitForConnection();
					setupStreams();
					whileChatting();
					
				}catch(EOFException eofException) {
					showMessage("\n Server ended the connection! ");
				}finally {
					closeStuff();
				}
			}
			
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}

	//during the chat conversation
	private void whileChatting() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n idk wtf that user sent!");
			}
			
		}while(!message.equals("CLIENT - END"));
	}
	
	//close streams and sockets after you are done chatting
	private void closeStuff() {
		showMessage("\n Closing connections... \n");
		ableToType(false);
		
		try {
			output.close();
			input.close();
			connection.close();
			
		}catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}

	//get stream to send and recieve data
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now setup! \n");	
	}

	//wait for connection, then display connection information
	private void waitForConnection() throws IOException {
		chatWindow.setEditable(false);
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept();
		showMessage(" Now connected to " + connection.getInetAddress().getHostName());	
	}
	
	//send a message to client
	private void sendMessage(String message) {
		try {
			output.writeObject("SERVER - " + message);
			output.flush();
			showMessage("\nSERVER - " + message);
			
		}catch(IOException ioException) {
			chatWindow.append("\n ERROR: I CANT SEND THAT MESSAGE");
		}
	}
	
	//updates chatWindow
	private void showMessage(final String text) {
		SwingUtilities.invokeLater(
				//THREAD
				new Runnable() {
				public void run() {
					chatWindow.append(text);
				}
			}
		);
	}
	
	//let the user type stuff into the textfield
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				//THREAD
				new Runnable() {
				public void run() {
					userText.setEditable(tof);
				}
			}
		);
	}
	
}
