import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import java.io.*;
import com.google.gson.Gson;

public class TradingHall {
	public static void main(String[] args) {
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
				System.out.print(
						"Sign Up Success!\n" + "Your account:" + account + "\nYour wallet id:" + wallet.getUUID() + '\n');
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
			System.out.print("1. Wallet Info\n" + "2. Make Trascation\n" + "3. Exit\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1:
//				viewWalletInfoPage(wallet_id);
				break;
			case 2:
				break;
			case 3:
				System.exit(0);
			default:
				break;
			}
		}

		// Choose User to transaction
		// make transaction
		// Verify transaction
		// make block
		// Verify block
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
		
		Gson gson = new Gson();
		String json = gson.toJson(wallet);
		
		try {
			FileUtils.write(new File(String.format("./data/wallet/%s.txt", wallet.getUUID())), json, "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				
				String json = null;
				try {
					json = FileUtils.readFileToString(new File(String.format("./data/wallet/%s.txt", wallet_id)), "UTF-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Wallet wallet = gson.fromJson(json, Wallet.class);
				
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

	private static void viewWalletInfoPage(String wallet_id) {
		Scanner scanner = new Scanner(System.in);
		boolean exit_page = false;
		while (!exit_page) {
			System.out.println("========================================");
			System.out.println("=          Wallet Information          =");
			System.out.println("========================================");
			System.out.print("1. List All Address\n" + "2. Generate New Address\n" + "3. Exit\n");
			int user_action = scanner.nextInt();
			
			switch (user_action) {
			case 1:
				
				break;
			case 2:
				break;
			case 3:
				exit_page = true;
				break;
			default:
				break;
			}

		}
	}
}