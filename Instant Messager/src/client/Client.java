package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField userInput;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	//constructor
	public Client(String host) {
		super("Instant Message Client");
		serverIP = host;
		userInput = new JTextField();
		userInput.setEditable(false);
		userInput.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						sendMessage(event.getActionCommand());
						userInput.setText("");
					}
				}	
		);
		add(userInput, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(800, 600);
		setVisible(true);
		chatWindow.setEditable(false);
	}
	
	//connect to server
	public void startServer() {
		try {
			serverConnect();
			setupStream();
			whileChatting();
			
		}catch(EOFException eofException) {
			showMessage("\n Client terminated the connection");
			
			}catch(IOException ioException) {
				ioException.printStackTrace();
		}finally {
			closeStuff();
		}
	}
	

	//connect to server
	private void serverConnect() throws IOException {
		showMessage("Attempting to connect... \n");
		connection = new Socket (InetAddress.getByName(serverIP),6789);
		showMessage("You are now connected to: " + connection.getInetAddress().getHostName());
		
	}

	//closes streams and sockets
	private void closeStuff() {
		showMessage("\n Closing stuff...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
			
		}catch (IOException ioException) {
			ioException.printStackTrace();
	}
}
		
	//set up streams to send and receive messages
	private void setupStream() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n You are now good to go! \n");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException) {
				showMessage("\n I don't know that object");
			}
			
		}while(!message.equals("SERVER - END"));
	}
	
	//sends messages to the server
	private void sendMessage(String message) {
		try {
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
			
		}catch (IOException ioException) {
			chatWindow.append("\n Something got messed up while sending the message");
		}
	}
	
	//shows messages in the chatWindow
	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						chatWindow.append(message);
				}
			}
		);
	}
	
	//gives user the permission to type in the TextField
	private void ableToType(final boolean tof) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						userInput.setEditable(tof);
				}
			}
		);
	}
}
