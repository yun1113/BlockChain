import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.commons.codec.digest.DigestUtils;

public class Block {

	private String block_hash = null;
	private String difficulty = "000"; // sha256 hash need three 0s ahead
	private long timestamp = System.currentTimeMillis() / 1000L;
	private int nonce = 0;
	private String prev_block_hash = null;
	private ArrayList<String> transaction_list = new ArrayList<String>();
	private String next_block_hash = "";

	public Block(String prev_block_hash, ArrayList<String> transaction_list) {
		this.transaction_list = transaction_list;
		this.prev_block_hash = prev_block_hash;

		boolean transaction_valid = true;
		for (String t : transaction_list) {
			Transaction trans = HandlingObj.getTransaction(t);
			if (!validateTransactionSign(trans)) {
				break;
			}
			if (!validateTransactionOrigin(trans)) {
				break;
			}
		}
		if (transaction_valid) {
			generateNonce();
		} else {
			System.out.println("Transaciton Invalid");
		}
		updateTransaction();
	}

	public Block(String block_hash, long timestamp, int nonce, String prev_block_hash,
			ArrayList<String> transaction_list, String next_block_hash) {
		this.block_hash = block_hash;
		this.timestamp = timestamp;
		this.nonce = nonce;
		this.prev_block_hash = prev_block_hash;
		this.transaction_list = transaction_list;
		this.next_block_hash = next_block_hash;
	}

	// first block
	public Block(String first_trans) {
		this.prev_block_hash = "0000000000000000000000000000000000000000000000000000000000000000";
		transaction_list.add(first_trans);
		generateNonce();
		updateTransaction();
	}

	private void updateTransaction() {
		for (String t : transaction_list) {
			Transaction trans = HandlingObj.getTransaction(t);
			trans.setBlockID(block_hash);
			HandlingObj.savingTransaction(trans);
		}
	}

	private void generateNonce() {
		String hash_string = prev_block_hash + timestamp;
		for (String t : transaction_list) {
			Transaction trans = HandlingObj.getTransaction(t);
			hash_string += trans.getTransactionHash();
		}
		int nonce = 0;
		while (true) {
			hash_string += Integer.toString(nonce);
			String sha256hex = DigestUtils.sha256Hex(hash_string);
			String double_sha256hex = DigestUtils.sha256Hex(sha256hex);
			if (double_sha256hex.startsWith(difficulty)) {
				this.nonce = nonce;
				this.block_hash = double_sha256hex;
				break;
			}
			nonce += 1;
		}
	}

	// traceback to coint origin
	private boolean validateTransactionOrigin(Transaction trans) {
		if (trans.getCoinBase()) {
			return true;
		} else {
			String addr = trans.getInputList().get(1).get("address");
			Address a = HandlingObj.getAddress(addr);
			Transaction t = HandlingObj.getTransaction(a.getTransactionList().get(-2));
			if (validateTransactionOrigin(t)) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean validateTransactionSign(Transaction trans) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		String trans_hash = trans.getTransactionHash();
		String sign_addr = null;
		boolean result = false;
		try {
			sign_addr = trans.getInputList().get(0).get("address");
		} catch (IndexOutOfBoundsException e) {
			sign_addr = trans.getOutputList().get(0).get("address");
		}
		Address addr = HandlingObj.getAddress(sign_addr);
		String sign_pub_key = addr.getPublicKey();

		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("ECDSA", "BC");
			// handling public key
			byte[] pub_key_byte = Base64.getDecoder().decode(sign_pub_key.getBytes());
			X509EncodedKeySpec ks2 = new X509EncodedKeySpec(pub_key_byte);
			PublicKey pk = kf.generatePublic(ks2);

			// generate signature
			Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA", "BC");
			ecdsaVerify.initVerify(pk);
			ecdsaVerify.update(trans_hash.getBytes("UTF-8"));

			byte[] signature = Base64.getDecoder().decode(trans.getSignSig().getBytes());
			result = ecdsaVerify.verify(signature);

		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public String getHashPrevBlock() {
		return prev_block_hash;
	}

	public long getTime() {
		return timestamp;
	}

	public int getNonce() {
		return nonce;
	}

	public String getBlockHash() {
		return block_hash;
	}

	public long getTimeStamp() {
		return timestamp;
	}

	public String getPrevBlockHash() {
		return prev_block_hash;
	}

	public ArrayList<String> getTransactionList() {
		return transaction_list;
	}
	
	public void setNextBlockHash(String next_block_hash){
		this.next_block_hash = next_block_hash;
	}
	
	public String getNextBlockHash(){
		return next_block_hash;
	}
}
