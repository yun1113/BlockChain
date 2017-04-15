
import java.io.*;

import org.apache.commons.io.FileUtils;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;

import com.google.gson.Gson;
import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class Wallet {
	private String uuid = null;
	private int address_num = 0;
	private String account = null;
	private ArrayList<String> address_list = new ArrayList<String>();

	public Wallet(String account) {
		generateWallet(account);
	}

	private void generateWallet(String account) {
		this.account = account;
		File f = null;

		// generate unique uuid
		while (true) {
			uuid = UUID.randomUUID().toString();

			f = new File(String.format("./data/wallet/%s.txt", uuid));
			if (f.exists() && !f.isDirectory()) {
				continue;
			} else {
				break;
			}
		}
	}

	public String getUUID() {
		return uuid;
	}

	public String generateNewAddress() {
		String addr = null;
		try {
			// key generate
			Gson gson = new Gson();
			ECKey ceKey = new ECKey();
			NetworkParameters params = TestNet3Params.get();
			ceKey.getPublicKeyAsHex();
			ceKey.toAddress(params).toBase58();
			
			Map<String, Object> keypair = new HashMap<String, Object>(); 
			keypair.put("pri_key", ceKey.getPrivateKeyAsHex());
			keypair.put("pub_key", ceKey.getPublicKeyAsHex());
			addr = ceKey.toAddress(params).toBase58();
			keypair.put("addr", addr);
			
			String json = gson.toJson(keypair);			
			try {
				FileUtils.write(new File(String.format("./data/key/%s.txt", addr)), json, "UTF-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			address_num += 1;
			address_list.add(addr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return addr;
	}

	public ArrayList<String> getAddress() {
		return address_list;
	}

	public int getAddressNum(){
		return address_num;
	}

	public String getAccount(){
		return account;
	}
}
