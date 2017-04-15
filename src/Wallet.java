import java.security.*;
import java.io.*;
import org.apache.commons.io.FileUtils;

public class Wallet {

	public Wallet() {
		
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
