import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.Sha256Hash;

public class Test {

	public static void main(String[] args) {
		String hash_string = "Hello, world";
		String sha256hex = org.apache.commons.codec.digest.DigestUtils.sha256Hex(hash_string);
		System.out.println(sha256hex);
	}

}
