import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.Sha256Hash;

public class Block {

	private String block_id = null;
	private Block prev_block = null;
	private String merkle_root = null;
	private BigInteger difficulty = null;
	private String timestamp = null;
	private int nonce = 0;
	private String hash_prev_block = null;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();

	public Block(Block parent_block) {
		this.prev_block = prev_block;
	}

	private void validateBlock() {
		String hash_string = null;
		// TODO need signsig
		hash_string = prev_block + merkle_root + timestamp;
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

	private void hashMarkleRoot(ArrayList<Transaction> transaction_list) {

	}

	private void hashPrevBlock(Block prev_block) {
		String hash_string = prev_block.getHashPrevBlock() + prev_block.getMarkleRoot() + prev_block.getTime()
				+ Integer.toString(prev_block.getNonce());
		String sha256hex = DigestUtils.sha256Hex(hash_string);
		String double_sha256hex = DigestUtils.sha256Hex(sha256hex);
		this.hash_prev_block = double_sha256hex;
	}

	// traceback to coint origin
	private void validateAllTransaction() {

	}
	
	public String getHashPrevBlock(){
		return hash_prev_block;
	}
	
	public String getMarkleRoot(){
		return merkle_root;
	}
	
	public String getTime(){
		return timestamp;
	}
	
	public int getNonce(){
		return nonce;
	}

}
