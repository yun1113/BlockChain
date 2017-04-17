import java.math.BigInteger;
import java.util.ArrayList;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;

public class Transaction {
	
	private String transcation_id =null;
	private String version = null;
	private int input_count = 0;
	private int output_count = 0;
	private ArrayList<Address> input_list = new ArrayList<Address>();
	private ArrayList<Address> output_list = new ArrayList<Address>();
	private String timestamp = null;
	private int total_value = 0;
	private ECKey.ECDSASignature signSig = null;

	public Transaction(ArrayList<Address> input_list, ArrayList<Address> output_list, int total_value) {
		this.input_list = input_list;
		this.output_list = output_list;
		this.total_value = total_value;
	}
	
	public void signTransaction(){
		String sign_pri_key = input_list.get(0).getPrivateKey();
		
		String hash_string = null;
		for(Address addr: input_list){
			hash_string += addr.getAddress();
		}
		for(Address addr: output_list){
			hash_string += addr.getAddress();
		}
		hash_string += Integer.toString(total_value);
		
		Sha256Hash hash = Sha256Hash.wrap(hash_string);
		ECKey pri_key = ECKey.fromPrivate(new BigInteger(sign_pri_key, 16));
		ECKey.ECDSASignature signSig = pri_key.sign(hash);

		this.signSig = signSig;
	}
	
	public void validateTransaction(){
		// TODO
	}
}
