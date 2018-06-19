package server;

import javax.swing.JFrame;

public class RunServer {

	public static void main(String[] args) {
		Server roar = new Server();
		roar.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		roar.startServer();

	}

}
