import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.Sha256Hash;

public class Block {

	private String block_id = null;
	private Block prev_block = null;
	private String merkle_root = null;
	private String difficulty = "000"; // sha256 hash need three 0 aheads
	private long timestamp = System.currentTimeMillis() / 1000L;
	private int nonce = 0;
	private String prev_block_hash = null;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();

	public Block(Block parent_block) {
		this.prev_block = prev_block;
	}

	private void generateNonce() {
		String hash_string = prev_block_hash + merkle_root + timestamp;
		int nonce = 0;
		while (true) {
			hash_string += Integer.toString(nonce);
			String sha256hex = DigestUtils.sha256Hex(hash_string);
			String double_sha256hex = DigestUtils.sha256Hex(sha256hex);
			if (double_sha256hex.startsWith(difficulty)){
				this.nonce = nonce;
				break;
			}
			nonce += 1;
		}
	}

	private void generateHashMarkleRoot(ArrayList<Transaction> transaction_list) {

	}

	private void generateHashPrevBlock(Block prev_block) {
		String hash_string = prev_block.getHashPrevBlock() + prev_block.getMarkleRoot() + prev_block.getTime()
				+ Integer.toString(prev_block.getNonce());
		String sha256hex = DigestUtils.sha256Hex(hash_string);
		String double_sha256hex = DigestUtils.sha256Hex(sha256hex);
		this.prev_block_hash = double_sha256hex;
	}

	// traceback to coint origin
	private void validateAllTransaction() {

	}
	
	public String getHashPrevBlock(){
		return prev_block_hash;
	}
	
	public String getMarkleRoot(){
		return merkle_root;
	}
	
	public long getTime(){
		return timestamp;
	}
	
	public int getNonce(){
		return nonce;
	}

}
