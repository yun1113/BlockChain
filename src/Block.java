import java.math.BigInteger;
import java.util.ArrayList;

import org.bitcoinj.core.Sha256Hash;

public class Block {

	private String block_id = null;
	private String parent_block = null;
	private String merkle_root = null;
	private BigInteger difficulty = null;
	private String timestamp = null;
	private int nonce = 0;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();

	public Block(String parent_block) {
		this.parent_block = parent_block;
	}

	private void validateBlock() {
		String hash_string = null;
		// TODO need signsig
		hash_string = parent_block + merkle_root + timestamp;
		int nonce = 0;
		while (true) {
			Sha256Hash hash = Sha256Hash.wrap(hash_string + nonce);
			if (hash.toBigInteger().compareTo(difficulty) < 0) {
				this.nonce = nonce;
				break;
			}
			nonce += 1;
		}
	}
	
	// traceback to coint origin
	private void validateAllTransaction() {
		
	}

}
