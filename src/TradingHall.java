import java.util.Scanner;
import java.io.*;

public class TradingHall {
	public static void main(String[] args) {
		
		// Login
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Please enter your account:");
		String account = scanner.next();
		
		System.out.println("Please enter your password:");
		String password = scanner.next();

		boolean check_identity = checkIdentity(account, password);
		if(check_identity){
			System.out.print("Welcome");
		}
		
		// Choose User to transaction
		// make transaction
		// Verify transaction
		// make block
		// Verify block
	}
	
	private static boolean checkIdentity(String account, String password){
		try {
			String file_path = String.format("./data/wallet/%s.txt", account);  
			BufferedReader br = new BufferedReader(new FileReader(file_path));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        line = br.readLine();
		    }
		    String check = sb.toString();
		    br.close();
		    
		    if(check.equals(password)){
		    	return true;
	    	}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}