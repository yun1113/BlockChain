import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BoostrapServer {

	private ArrayList<HandleAClient> client_list = new ArrayList<HandleAClient>();
	private ArrayList<String> connection_list = new ArrayList<String>();
	private ArrayList<String> log_list = new ArrayList<String>();
	private ExecutorService es;
	private ServerSocket server;
	private InetAddress addr;
	private int CONECTION_SIZE = 20;
	private int NEIGHBOR_NUMBER = 2;

	public static void main(String[] args) {
		BoostrapServer bs = new BoostrapServer();
		bs.excute();
	}

	private void list_peer_status() {
		for (String i : connection_list) {
			System.out.println(i + " active");
		}
	}
	
	private void list_log() {
		for (String i : log_list) {
			System.out.println(i);
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
			System.out.print("1. List peer status\n" + "2. Log\n");
			int user_action = scanner.nextInt();
			if (user_action == 1) {
				list_peer_status();
			} 
			else if (user_action == 2) {
				list_log();
			} else {
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
				server = new ServerSocket(20000, CONECTION_SIZE, addr);
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
						log_list.add("Reveived from client: " + self_display + " List Request");
						if (!connection_list.contains(self_display)){
							connection_list.add(self_display);
						}
						
						ArrayList<String> con_list = new ArrayList<String>(connection_list);

						// remove self and get random
						con_list.remove(self_display);
						Collections.shuffle(con_list);

						String json;
						if (con_list.size() < NEIGHBOR_NUMBER) {
							json = new Gson().toJson(con_list);
						} else {
							json = new Gson().toJson(con_list.subList(0, 2));
						}
						outputToClient.writeUTF(json);
					} else {
						Type typeOfHashMap = new TypeToken<Map<String, String>>() {
						}.getType();
						Map<String, String> map = new Gson().fromJson(sentence, typeOfHashMap);
						String message = map.get("message");
						String content = map.get("content");

						log_list.add("Reveived from client: " + self_display + " send " + message + " data");
						if(message.equals("Exit")){
							connection_list.remove(content);
						}
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
