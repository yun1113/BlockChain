import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TradingHall {
	public ArrayList<String> neighbor_list = new ArrayList<String>();
	private ArrayList<HandlePeerClient> client_list = new ArrayList<HandlePeerClient>();
	private ExecutorService worker;
	private ExecutorService es = Executors.newFixedThreadPool(20);;
	private ServerSocket server;
	private InetAddress addr;
	private int CONECTION_SIZE = 20;
	private int NEIGHBOR_NUMBER = 2;
	private int TTL = 5;

	public static void main(String[] args) {

		// initialize
		new TradingHall();

		Scanner scanner = new Scanner(System.in);
		Wallet wallet = null;

		// Index
		boolean exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=        Welcome to Trading Hall       =");
			System.out.println("========================================");
			System.out.print("1. Sign Up\n" + "2. Log in\n" + "3. Exit\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1: // Sign Up
				String account = signUp();
				wallet = createWallet(account);
				System.out.print("Sign Up Success!\n" + "Your account:" + account + "\nYour wallet id:"
						+ wallet.getUUID() + '\n');
				exit_page = true;
				break;
			case 2: // Log In
				wallet = logIn();
				if (wallet != null) {
					exit_page = true;
				}
				break;
			case 3: // Exit
				System.exit(0);
			default:
				break;
			}
		}

		// Main page
		exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=       Please Choose your action      =");
			System.out.println("========================================");
			System.out.print("1. Wallet Info\n" + "2. Trascation Info\n" + "3. Exit\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1:
				viewWalletInfoPage(wallet);
				break;
			case 2:
				viewTransactionPage(wallet);
				break;
			case 3:
				System.exit(0);
			default:
				break;
			}
		}
	}

	public TradingHall() {
		worker = Executors.newFixedThreadPool(20);
		worker.execute(new PeerServer());
	}

	private static String signUp() {
		Scanner scanner = new Scanner(System.in);
		String account = null;

		// Check whether account already exist
		while (true) {
			System.out.println("Account:");
			account = scanner.next();

			File f = new File(String.format("./data/account/%s.txt", account));
			if (f.exists() && !f.isDirectory()) {
				System.out.println("Account repeat");
				continue;
			} else {
				break;
			}
		}

		System.out.println("Password:");
		String password = scanner.next();

		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("account", account);
		map.put("password", password);
		String json = gson.toJson(map);

		try {
			FileUtils.write(new File(String.format("./data/account/%s.txt", account)), json, "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return account;
	}

	private static Wallet createWallet(String account) {
		Wallet wallet = new Wallet(account);
		wallet.generateWallet();
		HandlingObj.savingWallet(wallet);
		return wallet;
	}

	private static Wallet logIn() {
		Scanner scanner = new Scanner(System.in);
		Gson gson = new Gson();

		// Check whether login success
		while (true) {
			System.out.println("Please enter your wallet id:");
			String wallet_id = scanner.next();

			System.out.println("Please enter your password:");
			String password = scanner.next();

			boolean check_identity = checkIdentity(wallet_id, password);

			if (check_identity) {
				System.out.println("Welcome");
				Wallet wallet = HandlingObj.getWallet(wallet_id);
				return wallet;
			} else {
				System.out.println("Please enter correct informaions");
			}
		}
	}

	private static boolean checkIdentity(String wallet_id, String password) {
		try {
			String wallet_path = String.format("./data/wallet/%s.txt", wallet_id);
			BufferedReader br = new BufferedReader(new FileReader(wallet_path));
			Map<String, Object> wallet_map = new Gson().fromJson(br, Map.class);
			String account = (String) wallet_map.get("account");

			String account_path = String.format("./data/account/%s.txt", account);
			br = new BufferedReader(new FileReader(account_path));
			Map<String, Object> account_map = new Gson().fromJson(br, Map.class);
			br.close();

			String check_password = (String) account_map.get("password");
			if (check_password.equals(password)) {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void viewWalletInfoPage(Wallet wallet) {
		Scanner scanner = new Scanner(System.in);
		boolean exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=          Wallet Information          =");
			System.out.println("========================================");
			System.out.print("1. Wallet Total Value\n" + "2. List All Address\n" + "3. Return\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1:
				System.out.println(wallet.getTotalValue());
				break;
			case 2:
				ArrayList<Address> addr_list = wallet.getRealAddressList();
				for (Address addr : addr_list) {
					System.out.println(addr.getAddress());
				}
				break;
			case 3:
				exit_page = true;
				break;
			default:
				break;
			}

		}
	}

	private static void viewTransactionPage(Wallet wallet) {
		Scanner scanner = new Scanner(System.in);
		boolean exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=        Transaction Information       =");
			System.out.println("========================================");
			System.out.print("1. Send Bitcoin\n" + "2. Receive Bitcoin\n" + "3. Return\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1: // make transactions

				System.out.println("Input output address:");
				String output_addr = scanner.next();
				Address output_address = HandlingObj.getAddress(output_addr);

				System.out.println("Input output value:");
				int output_value = scanner.nextInt();

				if (wallet.getTotalValue() >= output_value) {
					Transaction trans = new Transaction(wallet, output_address, output_value);
					HandlingObj.savingTransaction(trans);
				} else {
					System.out.println("You do not have enough money");
				}

				break;
			case 2:
				Address new_addr = wallet.generateNewAddress();
				System.out.println("Use this address '" + new_addr.getAddress() + "' to receive bitcoin.");
				HandlingObj.savingAddress(new_addr);
				HandlingObj.savingWallet(wallet);
				break;
			case 3:
				exit_page = true;
				break;
			default:
				break;
			}

		}
	}

	// Define he thread class for receive message
	class PeerServer implements Runnable {

		private String serverIP = "127.0.0.1";
		private int serverPort = 10000;
		Socket socket;
		
		/** Run a thread */
		public void run() {
			try {
				// Create a socket to connect to the server
				socket = new Socket(serverIP, serverPort);

				/** ====== Get Neighbor List ====== */
				// Create an output stream to send data to server
				DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
				outputToServer.writeUTF("List");

				// Create an input stream to receive data from server
				DataInputStream inputFromServer = new DataInputStream(socket.getInputStream());
				String server_return_list = inputFromServer.readUTF();
				System.out.println("Server Response: " + server_return_list);

				// get neighbor list
				neighbor_list = new Gson().fromJson(server_return_list, new TypeToken<ArrayList<String>>() {
				}.getType());
				/** ====== /Get Neighbor List ====== */

				InetAddress local_ip = InetAddress.getByName("127.0.0.1");
				int local_port = socket.getLocalPort();

				socket.close();

				// set server
				try {
					server = new ServerSocket(local_port, CONECTION_SIZE, local_ip);
				} catch (IOException io) {
					io.printStackTrace();
					System.exit(1);
				}

				// Server wait client for connection
				for (int i = 0; i < CONECTION_SIZE; i++) {
					try {
						client_list.add(new HandlePeerClient(server.accept(), i));
						es.execute(client_list.get(i));
					} catch (IOException io) {
						io.printStackTrace();
						System.exit(1);
					}
				}

			} catch (ConnectException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}finally{
				try {
					socket.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	// Define he thread class for handle message
	class HandlePeerClient implements Runnable {
		private int client_id;
		private Socket socket; // a connected socket

		/** construct a thread */
		public HandlePeerClient(Socket socket, int num) {
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
				String peerClientIP = inetAddress.getHostAddress();
				int peerClientPort = socket.getPort();

				while (true) {
					String json = inputFromClient.readUTF();
					Type typeOfHashMap = new TypeToken<Map<String, String>>() {
					}.getType();
					Map<String, String> map = new Gson().fromJson(json, typeOfHashMap); 
					int ttl = Integer.parseInt(map.get("TTL")) - 1;
					String message = map.get("message");

					String self_display = peerClientIP + ":" + Integer.toString(peerClientPort);
					if (message.equals("Hello")) {
						System.out.println("Reveived from client: " + self_display + " Say Hello");
						if (ttl != 0) {
							worker.execute(new PeerClient(ttl, message)); // broadcast
						}
					}
					// if (sentence.equals("List")) {
					// System.out.println("Reveived from client: " +
					// self_display + " List Request");
					//
					// ArrayList<String> connection_list = new
					// ArrayList<String>();
					// for (HandlePeerClient i : client_list) {
					// if (i != null) {
					// connection_list.add(i.getConnectionDisplay());
					// }
					// }
					//
					// // remove self and get random
					// connection_list.remove(self_display);
					// Collections.shuffle(connection_list);
					//
					// String json;
					// if (connection_list.size() < NEIGHBOR_NUMBER) {
					// json = new Gson().toJson(connection_list);
					// } else {
					// json = new Gson().toJson(connection_list.subList(0, 2));
					// }
					// outputToClient.writeUTF(json);
					// }
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

	// Define he thread class for broadcast message
	class PeerClient implements Runnable {
		Socket socket;
		DataOutputStream outputToLink;
		DataInputStream inputFromLink;
		int ttl;
		String message;

		public PeerClient(int ttl, String message) {
			this.ttl = ttl;
			this.message = message;
		}

		@Override
		public void run() {
			try {

				// broadcast message
				for (String neighbor : neighbor_list) {
					
					// Create a socket to connect to the peer
					String[] parts = neighbor.split(":");
					String ip = parts[0]; // 004
					String port = parts[1]; // 034556

					socket = new Socket(ip, Integer.parseInt(port));
					outputToLink = new DataOutputStream(socket.getOutputStream());
					inputFromLink = new DataInputStream(socket.getInputStream());

					// output json data
					Map<String, String> map = new HashMap<String, String>();
					map.put("TTL", Integer.toString(ttl));
					map.put("message", message);
					String json = new Gson().toJson(map);
					outputToLink.writeUTF(json);
				}

			} catch (ConnectException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (inputFromLink != null)
						inputFromLink.close();
					if (outputToLink != null)
						outputToLink.close();
					if (this.socket != null && !this.socket.isClosed())
						this.socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}