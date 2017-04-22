import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;

public class Transaction {

	private String transcation_id = null;
	private ArrayList<Map<String, String>> input_list = new ArrayList<Map<String, String>>();
	private ArrayList<Map<String, String>> output_list = new ArrayList<Map<String, String>>();
	private long timestamp = System.currentTimeMillis() / 1000L;
	private String signSig = null;
	private boolean coinbase;

	public Transaction(Wallet wallet, Address output_address, int value) {
		output_list.add(transaction_detail(value, output_address.getAddress()));
		geneateInputList(wallet, value);
	}

	public Map<String, String> transaction_detail(int value, String address) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("value", Integer.toString(value));
		map.put("address", address);
		return map;
	}

	private void geneateInputList(Wallet wallet, int value) {
		ArrayList<Address> address_list = wallet.getAddresses();
		ArrayList<Map<String, String>> input_address_list = null;
		int temp_value = value;
		int index = 0;

		for (Address addr : address_list) {
			if (addr.getValue() > 0) {
				int change = addr.getValue() - temp_value;
				if (change > 0) {
					input_address_list.add(transaction_detail(addr.getValue(), addr.getAddress()));
					addr.setValue(0);
					// give change to self
					Address new_addr = wallet.generateNewAddress();
					output_list.add(transaction_detail(change, new_addr.getAddress()));
					new_addr.setValue(change);
					HandlingObj.savingWallet(wallet);
					HandlingObj.savingAddress(new_addr);
					break;
				} else if (change == 0) {
					input_address_list.add(transaction_detail(addr.getValue(), addr.getAddress()));
					addr.setValue(0);
					break;
				} else {
					input_address_list.add(transaction_detail(addr.getValue(), addr.getAddress()));
					addr.setValue(0);
					temp_value = change;
				}
				HandlingObj.savingAddress(addr);
			}
		}
	}

	public void signTransaction() {
		String first_addr = input_list.get(0).get("address");
		Address addr = HandlingObj.getAddress(first_addr);
		String sign_pri_key = addr.getPrivateKey();

		String hash_string = "";
		for (Map<String, String> map : input_list) {
			hash_string += map.get("address");
		}
		for (Map<String, String> map : output_list) {
			hash_string += map.get("address");
		}
		
		String sha256hex = DigestUtils.sha256Hex(hash_string);
		
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

	public ArrayList<Map<String, String>> getOutputList() {
		return output_list;
	}
}
