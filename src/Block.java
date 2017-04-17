import java.util.ArrayList;

public class Block {
	
	private String block_id =null;
	private String version = null;
	private String parent_block = null;
	private String merkle_root = null;
	private String difficulty = null;
	private String timestamp = null;
	private ArrayList<Transaction> transaction_list = new ArrayList<Transaction>(); 
	
	
	public Block(String parent_block) {
		this.parent_block = parent_block;
	}
	
	private void blockValidate(){
		
	}

}
