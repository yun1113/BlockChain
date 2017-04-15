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
				String wallet_id = createWallet(account);
				System.out.print(
						"Sign Up Success!\n" + "Your account:" + account + "\nYour wallet id:" + wallet_id + '\n');
				break;
			case 2: // Log In
				boolean login = logIn();
				if (login) {
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
			System.out.println("========================================\n");
			System.out.println("=       Please Choose your action      =\n");
			System.out.println("========================================\n");
			System.out.print("1. My Wallet\n" + "2. Make Trascation\n" + "3. Exit\n");
			int user_action = scanner.nextInt();

			switch (user_action) {
			case 1:
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

	private static String createWallet(String account) {
		Wallet wallet = new Wallet(account);
		return wallet.getUUID();
	}

	private static boolean logIn() {
		Scanner scanner = new Scanner(System.in);

		System.out.println("Please enter your wallet id:");
		String wallet_id = scanner.next();

		System.out.println("Please enter your password:");
		String password = scanner.next();

		boolean check_identity = checkIdentity(wallet_id, password);
		if (check_identity) {
			System.out.print("Welcome\n");
			return true;
		}

		return false;
	}

	private static boolean checkIdentity(String wallet_id, String password) {
		try {
			String file_path = String.format("./data/wallet/%s.txt", wallet_id);
			
			
			
			BufferedReader br = new BufferedReader(new FileReader(file_path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			String check = sb.toString();
			br.close();

			if (check.equals(password)) {
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void getMyWallet(){
		
	}
}