
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
	private ArrayList<Address> address_list = new ArrayList<Address>();

	public Wallet(String account) {
		this.account = account;
	}

	public void generateWallet() {
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

	public Address generateNewAddress() {
		Address new_addr = new Address(uuid);
		address_list.add(new_addr);
		return new_addr;
	}

	public ArrayList<Address> getAddresses() {
		return address_list;
	}

	public int getAddressNum(){
		return address_num;
	}

	public String getAccount(){
		return account;
	}

	public void setUUID(String uuid){
		this.uuid = uuid;
	}
	
	public void setAccount(String account){
		this.account = account;
	}
	
	public void setAddressNum(int address_num){
		this.address_num = address_num;
	}
	
	public void setAddressList(ArrayList<Address> address_list){
		this.address_list = address_list;
	}
}
