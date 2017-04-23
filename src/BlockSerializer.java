import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class BlockSerializer implements JsonSerializer<Block> {

	@Override
	public JsonElement serialize(Block block, Type arg1, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();
		result.add("block_hash", new JsonPrimitive(block.getBlockHash()));
		result.add("timestamp", new JsonPrimitive(block.getTimeStamp()));
		result.add("nonce", new JsonPrimitive(block.getNonce()));
		result.add("prev_block_hash", new JsonPrimitive(block.getPrevBlockHash()));
		result.add("next_block_hash", new JsonPrimitive(block.getNextBlockHash()));

		JsonArray jsonArray = new JsonArray();
		ArrayList<String> transaction_list = block.getTransactionList();
		if (transaction_list != null) {
			for (String value : transaction_list) {
				jsonArray.add(new JsonPrimitive(value));
			}
			result.add("transaction_list", jsonArray);
		}

		return result;
	}
}