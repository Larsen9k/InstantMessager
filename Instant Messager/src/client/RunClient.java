package client;

import javax.swing.JFrame;

public class RunClient {

	public static void main(String[] args) {
		Client roger;
		roger = new Client("127.0.0.1"); //<-- Server IP goes here
		roger.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		roger.startServer();

	}

}
