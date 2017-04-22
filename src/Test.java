import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.Sha256Hash;

public class Test {

	public static void main(String[] args) {
		String hash_string = "hehhe";
		String difficulty = "0000";
		int nonce = 0;
		while (true) {
			hash_string += Integer.toString(nonce);
			String sha256hex = DigestUtils.sha256Hex(hash_string);
			String double_sha256hex = DigestUtils.sha256Hex(sha256hex);
			if (double_sha256hex.startsWith(difficulty)){
				System.out.println(double_sha256hex);
				break;
			}
			nonce += 1;
		}
		System.out.println(nonce);
	}

}
