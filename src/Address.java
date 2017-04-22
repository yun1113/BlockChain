import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;

import com.google.gson.Gson;

public class Address {
	
	private String address = null;
	private String wallet_id = null;
	private String pub_key = null;
	private String pri_key = null;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();
	private int value = 0;
	
	public Address(String wallet_id) {
		generateAddress();
		this.wallet_id = wallet_id;
	}
	
	
	private void generateAddress() {
		try {
			// key generate
			Gson gson = new Gson();
			ECKey ceKey = new ECKey();
			NetworkParameters params = TestNet3Params.get();
			ceKey.toAddress(params).toBase58();
			
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			KeyPairGenerator g = KeyPairGenerator.getInstance("ECDSA", "BC");
			ECNamedCurveParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("B-571");
			g.initialize(ecSpec, new SecureRandom());
			KeyPair pair = g.generateKeyPair();
			
			byte[] pub_key_byte = pair.getPublic().getEncoded();
			byte[] pri_key_byte = pair.getPrivate().getEncoded();

			this.pub_key =  Base64.getEncoder().encodeToString(pub_key_byte);
			this.pri_key =  Base64.getEncoder().encodeToString(pri_key_byte);
			this.address = ceKey.toAddress(params).toBase58();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addTransaction(Transaction t){
		transaction_list.add(t);
	}
	
	public String getAddress(){
		return address;
	}

	public String getPublicKey(){
		return pub_key;
	}
	
	public String getPrivateKey(){
		return pri_key;
	}
	
	public String getWalletId(){
		return wallet_id;
	}
	
	public int getValue(){
		return value;
	}
	
	
	public ArrayList<Transaction> getTransactionList(){
		return transaction_list;
	}

	public void setAddress(String addr){
		this.address = addr;
	}
	public void setPublicKey(String pub_key){
		this.pub_key = pub_key;
	}
	public void setPrivateKey(String pri_key){
		this.pri_key = pri_key;
	}
	
	public void setValue(int value){
		this.value = value;
	}

}
