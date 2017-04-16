import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import com.google.gson.Gson;

public class Address {
	
	private String address = null;
	private String wallet_id = null;
	private String pub_key = null;
	private String pri_key = null;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>();
	
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
			ceKey.getPublicKeyAsHex();
			ceKey.toAddress(params).toBase58();
			
			this.pri_key = ceKey.getPrivateKeyAsHex();
			this.pub_key = ceKey.getPublicKeyAsHex();
			this.address = ceKey.toAddress(params).toBase58();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

	public void setAddress(String addr){
		this.address = addr;
	}
	public void setPublicKey(String pub_key){
		this.pub_key = pub_key;
	}
	public void setPrivateKey(String pri_key){
		this.pri_key = pri_key;
	}

}
