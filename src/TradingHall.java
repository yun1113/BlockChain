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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class TradingHall {
	public static ArrayList<String> neighbor_list = new ArrayList<String>();
	private ArrayList<HandlePeerClient> client_list = new ArrayList<HandlePeerClient>();
	private static ArrayList<String> log_list = new ArrayList<String>();
	private ExecutorService main_worker = Executors.newFixedThreadPool(5);;
	private static ExecutorService broadcast_worker = Executors.newFixedThreadPool(20);;
	private static ServerSocket server;
	private InetAddress addr;
	private int CONECTION_SIZE = 20;
	private int NEIGHBOR_NUMBER = 2;
	private static int TTL = 2;
	private ArrayList<String> trans_list = new ArrayList<String>();
	private static String first_block_hash = "000febab7dbd0a466fbc958e0a063642bfa7201bcb89b6687d753cae28024c50";
	private int TRANSACTION_NUM_IN_BLOCK = 1;

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
			System.out.print("1. Sign Up\n" + "2. Log in\n" + "3. List Block\n" + "4. Watch Block Data\n"
					+ "5. Watch Transaction Data\n" + "6. Exit\n" + "7. Test: Make transaction 1\n"
					+ "8. Test: Make transaction 2\n" + "9. Log\n" + "10. Test: Exit\n");
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
			case 3:
				String block_hash = first_block_hash;
				Block b = null;
				int counter = 1;
				while (true) {
					System.out.println(counter + " : " + block_hash);
					b = HandlingObj.getBlcok(block_hash);
					if (b.getNextBlockHash().equals("")) {
						break;
					}
					block_hash = b.getNextBlockHash();
					counter += 1;
				}
				break;
			case 4:
				String block_id = "";
				System.out.println("Input block ID:");
				while (block_id.equals("")) {
					block_id = scanner.nextLine();
				}
				Block block = HandlingObj.getBlcok(block_id);
				System.out.println("Block ID: " + block.getBlockHash());
				System.out.println("Prev Block ID: " + block.getPrevBlockHash());
				if (!block.getNextBlockHash().equals("")) {
					System.out.println("Next Block ID: " + block.getNextBlockHash());
				}
				System.out.println("Transacion list");
				for (String i : block.getTransactionList()) {
					System.out.println(i);
				}

				break;
			case 5:
				String transaction_id = "";
				System.out.println("Input transaction ID:");
				while (transaction_id.equals("")) {
					transaction_id = scanner.nextLine();
				}
				Transaction trans = HandlingObj.getTransaction(transaction_id);
				System.out.println("Transaction ID: " + trans.getTransactionHash());
				System.out.println("Block ID: " + trans.getBlockID());
				System.out.println("Input Address List: ");
				if (!(trans.getInputList().size() == 0)) {
					for (Map<String, String> i : trans.getInputList()) {
						System.out.println(i.get("address") + ":" + i.get("value"));
					}
				}
				System.out.println("output Address List: ");
				for (Map<String, String> i : trans.getOutputList()) {
					System.out.println(i.get("address") + ":" + i.get("value"));
				}
				break;
			case 6: // Exit
				System.exit(0);
			case 7:
				System.out.println("from wallet 7948f4a1-fbb0-4e7a-bd40-a445648758d8 to address mnA2RVLrorxkTvPNs12PGtT2X6rbrimMCG");
				Wallet w = HandlingObj.getWallet("7948f4a1-fbb0-4e7a-bd40-a445648758d8");
				Address output_address = HandlingObj.getAddress("mnA2RVLrorxkTvPNs12PGtT2X6rbrimMCG");

				int output_value = 1;

				if (w.getTotalValue() >= output_value) {
					Transaction transaction = new Transaction(w, output_address, output_value);
					HandlingObj.savingTransaction(transaction);

					String content = "";
					try {
						content = FileUtils.readFileToString(
								new File(String.format("./data/transaction/%s.txt", transaction.getTransactionHash())),
								"UTF-8");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					broadcast_worker.execute(new PeerClient(TTL, "Transaction", content)); // broadcast
				} else {
					System.out.println("You do not have enough money");
				}
				break;
			case 8:
				System.out.println("from wallet 7948f4a1-fbb0-4e7a-bd40-a445648758d8 to address mnA2RVLrorxkTvPNs12PGtT2X6rbrimMCG");
				Wallet w2 = HandlingObj.getWallet("7948f4a1-fbb0-4e7a-bd40-a445648758d8");
				Address output_address2 = HandlingObj.getAddress("mnA2RVLrorxkTvPNs12PGtT2X6rbrimMCG");

				int output_value2 = 1;

				if (w2.getTotalValue() >= output_value2) {
					Transaction transaction = new Transaction(w2, output_address2, output_value2);
					HandlingObj.savingTransaction(transaction);

					String content = "";
					try {
						content = FileUtils.readFileToString(
								new File(String.format("./data/transaction/%s.txt", transaction.getTransactionHash())),
								"UTF-8");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					broadcast_worker.execute(new PeerClient(TTL, "Transaction", content)); // broadcast
				} else {
					System.out.println("You do not have enough money");
				}
				break;
			case 9:
				for (String i : log_list) {
					System.out.println(i);
				}
				break;
			case 10:
				int local_port = server.getLocalPort();
				String local_ip = server.getInetAddress().getHostAddress();
				broadcast_worker.execute(new PeerClient(TTL, "Exit", local_ip + ":" + local_port)); // broadcast
				try {
					Thread.sleep(10000); // 10 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
				break;
			case 12:
				for (String i : neighbor_list) {
					System.out.println(i);
				}
				break;
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
				int local_port = server.getLocalPort();
				String local_ip = server.getInetAddress().getHostAddress();
				broadcast_worker.execute(new PeerClient(TTL, "Exit", local_ip + ":" + local_port)); // broadcast
				try {
					Thread.sleep(10000); // 10 seconds
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.exit(0);
			default:
				break;
			}
		}
	}

	// initial
	public TradingHall() {
		main_worker.execute(new PeerServer());
		main_worker.execute(new Miner());
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

					String content = "";
					try {
						content = FileUtils.readFileToString(
								new File(String.format("./data/transaction/%s.txt", trans.getTransactionHash())),
								"UTF-8");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					broadcast_worker.execute(new PeerClient(TTL, "Transaction", content)); // broadcast
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

	class Miner implements Runnable {

		@Override
		public void run() {
			while (true) {
				while (trans_list.size() < TRANSACTION_NUM_IN_BLOCK) {
					try {
						Thread.sleep(10000); // 10s
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				String prev_block_hash = get_prev_block();
				Block block = new Block(prev_block_hash, trans_list);
				Block prev_block = HandlingObj.getBlcok(prev_block_hash);
				
				if (!prev_block.getNextBlockHash().equals("")) {
					HandlingObj.savingBlock(block);

					String content = "";
					try {
						content = FileUtils.readFileToString(
								new File(String.format("./data/block/%s.txt", block.getBlockHash())), "UTF-8");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					broadcast_worker.execute(new PeerClient(TTL, "Block", content)); // broadcast
				}
				ArrayList<String> temp_lisit = new ArrayList<String>(block.getTransactionList());
				for (String i :temp_lisit) {
					trans_list.remove(i);
				}
			}
		}
	}

	private String get_prev_block() {
		String block_hash = first_block_hash;
		Block b = null;
		while (true) {
			b = HandlingObj.getBlcok(block_hash);
			if (b.getNextBlockHash().equals("")) {
				break;
			}
			block_hash = b.getNextBlockHash();
		}
		return b.getBlockHash();
	}

	// Define he thread class for receive message
	class PeerServer implements Runnable {

		private String serverIP = "127.0.0.1";
		private int serverPort = 20000;
		Socket socket;

		/** Run a thread */
		public void run() {
			try {
				// Create a socket to connect to the
				socket = new Socket(serverIP, serverPort);

				/** ====== Get Neighbor List ====== */
				while (neighbor_list.size() <= 1) {
					// Create an output stream to send data to server
					DataOutputStream outputToServer = new DataOutputStream(socket.getOutputStream());
					outputToServer.writeUTF("List");

					// Create an input stream to receive data from server
					DataInputStream inputFromServer = new DataInputStream(socket.getInputStream());
					String server_return_list = inputFromServer.readUTF();

					// get neighbor list
					neighbor_list = new Gson().fromJson(server_return_list, new TypeToken<ArrayList<String>>() {
					}.getType());
					neighbor_list.add(serverIP + ":" + Integer.toString(serverPort));
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
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
						broadcast_worker.execute(client_list.get(i));
					} catch (IOException io) {
						io.printStackTrace();
						System.exit(1);
					}
				}

			} catch (ConnectException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
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
					String content = map.get("content");

					String self_display = peerClientIP + ":" + Integer.toString(peerClientPort);
					if (message.equals("Transaction")) {
						log_list.add("Reveived from client: " + self_display + " send Transaction data");

						// content to transaction and save
						Gson gson = new GsonBuilder()
								.registerTypeAdapter(Transaction.class, new TransactionDeserializer()).create();
						Transaction transaction = gson.fromJson(content, Transaction.class);
						HandlingObj.savingTransaction(transaction);

						// if not repeat, add to transaction list
						if (!trans_list.contains(transaction.getTransactionHash())) {
							trans_list.add(transaction.getTransactionHash());
						}
					} else if (message.equals("Block")) {
						log_list.add("Reveived from client: " + self_display + " send Block data");

						// content to block and save
						Gson gson = new GsonBuilder().registerTypeAdapter(Block.class, new BlockDeserializer())
								.create();
						Block block = gson.fromJson(content, Block.class);

						if (verify_block(block)) {
							HandlingObj.savingBlock(block);
						} else {
							log_list.add("Block id " + block.getBlockHash() + " invalid");
						}
					} else if (message.equals("Exit")) {
						log_list.add("Reveived from client: " + self_display + " send Exit request");
						neighbor_list.remove(content);
					}

					if (ttl != 0) {
						broadcast_worker.execute(new PeerClient(ttl, message, content)); // broadcast
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

		public boolean verify_block(Block block) {
			String hash_string = block.getPrevBlockHash() + block.getTimeStamp();
			for (String t : block.getTransactionList()) {
				Transaction trans = HandlingObj.getTransaction(t);
				hash_string += trans.getTransactionHash();
			}
			int nonce = block.getNonce();

			hash_string += Integer.toString(nonce);
			String sha256hex = DigestUtils.sha256Hex(hash_string);
			String double_sha256hex = DigestUtils.sha256Hex(sha256hex);

			if (double_sha256hex.startsWith(block.getDifficaulty())) {
				return true;
			} else {
				return false;
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
	static class PeerClient implements Runnable {
		Socket socket;
		DataOutputStream outputToLink;
		DataInputStream inputFromLink;
		int ttl;
		String message;
		String content;

		public PeerClient(int ttl, String message, String content) {
			this.ttl = ttl;
			this.message = message;
			this.content = content;
		}

		@Override
		public void run() {
			try {

				// broadcast message
				for (String neighbor : neighbor_list) {

					// Create a socket to connect to the peer
					String[] parts = neighbor.split(":");
					String ip = parts[0];
					String port = parts[1];
					socket = new Socket(ip, Integer.parseInt(port));
					outputToLink = new DataOutputStream(socket.getOutputStream());
					inputFromLink = new DataInputStream(socket.getInputStream());

					// output json data
					Map<String, String> map = new HashMap<String, String>();
					map.put("TTL", Integer.toString(ttl));
					map.put("message", message);
					map.put("content", content);
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