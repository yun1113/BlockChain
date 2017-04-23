import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

public class Transaction {

	private String transcation_hash = null;
	private ArrayList<Map<String, String>> input_list = new ArrayList<Map<String, String>>();
	private ArrayList<Map<String, String>> output_list = new ArrayList<Map<String, String>>();
	private long timestamp = System.currentTimeMillis() / 1000L;
	private String signSig = null;
	private boolean coinbase = false;
	private String block_id = null;

	// coinbase
	public Transaction(Wallet wallet, int value) {
		Address coinbase = wallet.generateNewAddress();
		HandlingObj.savingAddress(coinbase);
		output_list.add(transaction_detail(value, coinbase.getAddress()));
		signTransaction();
		updateRelatedAddress();
		this.coinbase = true;
		HandlingObj.savingWallet(wallet);
	}

	public Transaction(Wallet wallet, Address output_address, int value) {
		output_list.add(transaction_detail(value, output_address.getAddress()));
		geneateInputList(wallet, value);
		signTransaction();
		updateRelatedAddress();
	}

	public Transaction(String transcation_hash, ArrayList<Map<String, String>> input_list,
			ArrayList<Map<String, String>> output_list, long timestamp, String signSig, boolean coinbase,
			String block_id) {
		this.transcation_hash = transcation_hash;
		this.input_list = input_list;
		this.output_list = output_list;
		this.timestamp = timestamp;
		this.signSig = signSig;
		this.coinbase = coinbase;
		this.block_id = block_id;
	}

	public Map<String, String> transaction_detail(int value, String address) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", Integer.toString(value));
		map.put("address", address);
		return map;
	}

	private void updateRelatedAddress() {
		if (input_list.size() != 0) {
			for (Map<String, String> map : input_list) {
				String addr_string = map.get("address");
				Address addr = HandlingObj.getAddress(addr_string);
				addr.setValue(0);
				addr.addTransaction(transcation_hash);
				HandlingObj.savingAddress(addr);
			}
		}
		if (output_list.size() != 0) {
			for (Map<String, String> map : output_list) {
				String addr_string = map.get("address");
				Address addr = HandlingObj.getAddress(addr_string);
				addr.addTransaction(transcation_hash);
				addr.setValue(Integer.parseInt(map.get("value")));
				HandlingObj.savingAddress(addr);
			}
		}
	}

	private void geneateInputList(Wallet wallet, int value) {
		ArrayList<Address> address_list = wallet.getRealAddressList();
		int temp_value = value;
		int index = 0;

		for (Address addr : address_list) {
			if (addr.getValue() > 0) {
				int change = addr.getValue() - temp_value;
				if (change > 0) {
					Map<String, String> m = transaction_detail(addr.getValue(), addr.getAddress());
					input_list.add(m);
					addr.setValue(0);
					// give change to self
					Address new_addr = wallet.generateNewAddress();
					output_list.add(transaction_detail(change, new_addr.getAddress()));
					new_addr.setValue(change);
					HandlingObj.savingWallet(wallet);
					HandlingObj.savingAddress(new_addr);
					break;
				} else if (change == 0) {
					input_list.add(transaction_detail(addr.getValue(), addr.getAddress()));
					addr.setValue(0);
					break;
				} else {
					input_list.add(transaction_detail(addr.getValue(), addr.getAddress()));
					addr.setValue(0);
					temp_value = change;
				}
				HandlingObj.savingAddress(addr);
			}
		}
	}

	public void signTransaction() {
		String sign_addr = null;
		try {
			sign_addr = input_list.get(0).get("address");
		} catch (IndexOutOfBoundsException e) {
			sign_addr = output_list.get(0).get("address");
		}
		Address addr = HandlingObj.getAddress(sign_addr);
		String sign_pri_key = addr.getPrivateKey();

		String hash_string = "";
		for (Map<String, String> map : input_list) {
			hash_string += map.get("address");
		}
		for (Map<String, String> map : output_list) {
			hash_string += map.get("address");
		}

		String sha256hex = DigestUtils.sha256Hex(hash_string);
		this.transcation_hash = sha256hex;

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		KeyFactory kf;
		try {
			kf = KeyFactory.getInstance("ECDSA", "BC");
			// handling private key
			byte[] pri_key_byte = Base64.getDecoder().decode(sign_pri_key.getBytes());
			PKCS8EncodedKeySpec ks2 = new PKCS8EncodedKeySpec(pri_key_byte);
			PrivateKey pk2 = kf.generatePrivate(ks2);

			// generate signature
			Signature ecdsaSign = Signature.getInstance("SHA256withECDSA", "BC");
			ecdsaSign.initSign(pk2);
			ecdsaSign.update(hash_string.getBytes("UTF-8"));
			byte[] signature = ecdsaSign.sign();
			this.signSig = Base64.getEncoder().encodeToString(signature);
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SignatureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void validateTransaction() {
		// TODO
	}

	public String getTransactionHash() {
		return transcation_hash;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getSignSig() {
		return signSig;
	}

	public boolean getCoinBase() {
		return coinbase;
	}

	public ArrayList<Map<String, String>> getInputList() {
		return input_list;
	}

	public ArrayList<Map<String, String>> getOutputList() {
		return output_list;
	}
	
	public String getBlockID() {
		return block_id;
	}

	
}
