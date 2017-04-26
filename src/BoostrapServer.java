import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.google.gson.Gson;

public class BoostrapServer {

	private ArrayList<HandleAClient> client_list = new ArrayList<HandleAClient>();
	private ExecutorService es;
	private ServerSocket server;
	private InetAddress addr;
	private int CONECTION_SIZE = 20;
	private int NEIGHBOR_NUMBER = 2;

	public static void main(String[] args) {
		BoostrapServer bs = new BoostrapServer();
		bs.excute();
	}
	
	private void list_peer_status(){
		for (HandleAClient i : client_list) {
			System.out.println(i.getConnectionDisplay() + " active");
		}
	}

	public BoostrapServer() {
		es = Executors.newFixedThreadPool(CONECTION_SIZE);
	}

	public void excute() {
		es.execute(new WaitConnectionThread());
		Scanner scanner = new Scanner(System.in);
		boolean exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=            BoostrapServer            =");
			System.out.println("========================================");
			System.out.print("1. List peer status\n");
			int user_action = scanner.nextInt();
			if(user_action == 1){
				list_peer_status();
			}
			else{
				System.out.print("Wrong Input");
			}
		}
	}

	class WaitConnectionThread implements Runnable {

		@Override
		public void run() {
			// set server portNumber and chatPerson
			try {
				addr = InetAddress.getByName("127.0.0.1");
				server = new ServerSocket(10000, CONECTION_SIZE, addr);
			} catch (IOException io) {
				io.printStackTrace();
				System.exit(1);
			}

			// Server wait client for connection
			for (int i = 0; i < CONECTION_SIZE; i++) {
				try {
					client_list.add(new HandleAClient(server.accept(), i));
					es.execute(client_list.get(i));
				} catch (IOException io) {
					io.printStackTrace();
					System.exit(1);
				}
			}
			
		}
		
	}
	// Inner class
	// Define he thread class for handling new connection
	class HandleAClient implements Runnable {
		private int client_id;
		private Socket socket; // a connected socket

		/** construct a thread */
		public HandleAClient(Socket socket, int num) {
			this.client_id = num + 1;
			this.socket = socket;
		}

		/** Run a thread */
		public void run() {
			try {
				// create data input and output streams
				DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
				DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());
				InetAddress inetAddress = socket.getInetAddress();
				String clientIP = inetAddress.getHostAddress();
				int clientPort = socket.getPort();

				while (true) {
					String sentence = inputFromClient.readUTF();
					String self_display = clientIP + ":" + Integer.toString(clientPort);
					if (sentence.equals("List")) {
						System.out.println("Reveived from client: " + self_display + " List Request");

						ArrayList<String> connection_list = new ArrayList<String>();
						for (HandleAClient i : client_list) {
							if (i != null) {
								connection_list.add(i.getConnectionDisplay());
							}
						}

						// remove self and get random
						connection_list.remove(self_display);
						Collections.shuffle(connection_list);

						String json;
						if (connection_list.size() < NEIGHBOR_NUMBER) {
							json = new Gson().toJson(connection_list);
						} else {
							json = new Gson().toJson(connection_list.subList(0, 2));
						}
						outputToClient.writeUTF(json);
					}
				}
			} catch (IOException e) {
				// System.err.println(e);
				try {
					socket.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

		public String getConnectionDisplay() {
			InetAddress inetAddress = socket.getInetAddress();
			String clientIP = inetAddress.getHostAddress();
			int clientPort = socket.getPort();

			return String.format("%s:%s", clientIP, Integer.toString(clientPort));
		}
	}

}
