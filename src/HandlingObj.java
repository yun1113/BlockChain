import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HandlingObj {

	public static void savingWallet(Wallet wallet){
		Gson gson = new GsonBuilder().registerTypeAdapter(Wallet.class, new WalletSerializer())
                .create();
		String json = gson.toJson(wallet);
		try {
			FileUtils.write(new File(String.format("./data/wallet/%s.txt", wallet.getUUID())), json, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void savingAddress(Address address){
		Gson gson = new GsonBuilder().registerTypeAdapter(Address.class, new AddressSerializer())
                .create();
		String json = gson.toJson(address);
		try {
			FileUtils.write(new File(String.format("./data/address/%s.txt", address.getAddress())), json, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Wallet getWallet(String wallet_id){
		Gson gson = new GsonBuilder().registerTypeAdapter(Wallet.class, new WalletDeserializer())
                .create();
		
		String json = null;
		try {
			json = FileUtils.readFileToString(new File(String.format("./data/wallet/%s.txt", wallet_id)), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Wallet wallet = gson.fromJson(json, Wallet.class);
		return wallet;
	}
	
	public static Address getAddress(String addr){
		Gson gson = new GsonBuilder().registerTypeAdapter(Address.class, new AddressDeserializer())
                .create();
		
		String json = null;
		try {
			json = FileUtils.readFileToString(new File(String.format("./data/address/%s.txt", addr)), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Address address = gson.fromJson(json, Address.class);
		return address;
	}
	
	public static void savingTransaction(Transaction transaction){
		Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionSerializer())
                .create();
		String json = gson.toJson(transaction);
		try {
			FileUtils.write(new File(String.format("./data/transaction/%s.txt", transaction.getTransactionHash())), json, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Transaction getTransaction(String transaction_hash){
		Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionDeserializer())
                .create();
		
		String json = null;
		try {
			json = FileUtils.readFileToString(new File(String.format("./data/transaction/%s.txt", transaction_hash)), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Transaction transaction = gson.fromJson(json, Transaction.class);
		return transaction;
	}
	
	public static void savingBlock(Block block){
		Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionSerializer())
                .create();
		String json = gson.toJson(block);
		try {
			FileUtils.write(new File(String.format("./data/block/%s.txt", block.getPrevBlockHash())), json, "UTF-8", false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Block getBlcok(String block_hash){
		Gson gson = new GsonBuilder().registerTypeAdapter(Transaction.class, new TransactionDeserializer())
                .create();
		
		String json = null;
		try {
			json = FileUtils.readFileToString(new File(String.format("./data/block/%s.txt", block_hash)), "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Block block = gson.fromJson(json, Block.class);
		return block;
	}

}
