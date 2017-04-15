import java.security.*;
import java.io.*;
import org.apache.commons.io.FileUtils;
import com.google.gson.Gson;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

public class Wallet {
	private String uuid = null;
	private int address_num = 0;

	public Wallet(String account) {
		generateWallet(account);
	}

	public void generateWallet(String account) {
		File f = null;
		Gson gson = new Gson();
		Map<String, Object> map = new HashMap<String, Object>();

		// generate unique uuid
		while (true) {
			uuid = UUID.randomUUID().toString();
			
			map.put("account", account);
			map.put("uuid", uuid);

			f = new File(String.format("./data/wallet/%s.txt", uuid));
			if (f.exists() && !f.isDirectory()) {
				continue;
			} else {
				break;
			}
		}
		String json = gson.toJson(map);
		
		try {
			FileUtils.write(f, json, "UTF-8", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getUUID() {
		return uuid;
	}

	public void saveRSAKey() {

		try {
			SecureRandom sr = new SecureRandom();
			KeyPairGenerator kg = KeyPairGenerator.getInstance("RSA");

			// generate 1024 bits key with random number
			kg.initialize(1024, sr);

			// write to file
			FileOutputStream fos = new FileOutputStream("./data/key/RSAKey.xml");
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(kg.generateKeyPair());
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static KeyPair getKeyPair() {
		KeyPair kp = null;
		try {

			String fileName = "./data/key/RSAKey.xml";
			InputStream is = FileUtils.class.getClassLoader().getResourceAsStream(fileName);

			ObjectInputStream oos = new ObjectInputStream(is);
			kp = (KeyPair) oos.readObject();
			oos.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return kp;

	}

}
