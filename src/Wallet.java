
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
		address_list.add(new_addr.getAddress());
		return new_addr;
	}

	public ArrayList<String> getAddresses() {
		return address_list;
	}

	public int getAddressNum(){
		return address_num;
	}

	public String getAccount(){
		return account;
	}

	public int getTotalValue(){
		int total = 0;
		for(String addr : address_list){
			Address a = HandlingObj.getAddress(addr);
			total += a.getValue();
		}
		return total;
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
	
	public void setAddressList(ArrayList<String> address_list){
		this.address_list = address_list;
	}
	
	public ArrayList<Address> getRealAddressList(){
		ArrayList<Address> addr_list = new ArrayList<Address>();
		for(String addr: address_list){
			Address a = HandlingObj.getAddress(addr);
			addr_list.add(a);
		}
		return addr_list;
	}
}
