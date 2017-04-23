import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TransactionSerializer implements JsonSerializer<Transaction> {

	@Override
	public JsonElement serialize(Transaction transaction, Type arg1, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();
		result.add("transcation_hash", new JsonPrimitive(transaction.getTransactionHash()));
		result.add("timestamp", new JsonPrimitive(transaction.getTimestamp()));
		result.add("signsig", new JsonPrimitive(transaction.getSignSig()));
		result.add("coinbase", new JsonPrimitive(transaction.getCoinBase()));
		result.add("block_ID", new JsonPrimitive(transaction.getBlockID()));
		
		JsonArray jsonArray = new JsonArray();
		ArrayList<Map<String, String>> input_list = transaction.getInputList();
		if (input_list != null) {
			for (Map<String, String> map : input_list) {
				Gson gson = new GsonBuilder().create();
				String json = gson.toJson(map);
				jsonArray.add(new JsonPrimitive(json));
			}
			result.add("input_list", jsonArray);
		}
		
		JsonArray output_jsonArray = new JsonArray();
		ArrayList<Map<String, String>> output_list = transaction.getOutputList();
		if (output_list != null) {
			for (Map<String, String> map : output_list) {
				Gson gson = new GsonBuilder().create();
				String json = gson.toJson(map);
				output_jsonArray.add(new JsonPrimitive(json));
			}
			result.add("output_list", output_jsonArray);
		}
		
		return result;
	}
}
