import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class TransactionDeserializer implements JsonDeserializer<Transaction> {

	@Override
	public Transaction deserialize(JsonElement json, java.lang.reflect.Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		
		final JsonObject jsonObject = json.getAsJsonObject();
		
		final JsonArray jsonAddressArray = jsonObject.get("input_list").getAsJsonArray();
	    final ArrayList<Map<String, String>> input_list = new ArrayList<Map<String, String>>();
	    for (int i = 0; i < jsonAddressArray.size(); i++) {
	      final String input = jsonAddressArray.get(i).getAsString();
	      Gson gson = new Gson();
	      Type typeOfHashMap = new TypeToken<Map<String, String>>(){}.getType();
	      Map<String, String> newMap = gson.fromJson(json, typeOfHashMap);
	      
	      input_list.add(newMap);
	    }
	    
	    final JsonArray output_jsonAddressArray = jsonObject.get("output_list").getAsJsonArray();
	    final ArrayList<Map<String, String>> output_list = new ArrayList<Map<String, String>>();
	    for (int i = 0; i < output_jsonAddressArray.size(); i++) {
	      final String input = output_jsonAddressArray.get(i).getAsString();
	      Gson gson = new Gson();
	      Type typeOfHashMap = new TypeToken<Map<String, String>>(){}.getType();
	      Map<String, String> newMap = gson.fromJson(json, typeOfHashMap);
	      
	      output_list.add(newMap);
	    }
	    
		final Transaction transaction = new Transaction(jsonObject.get("transcation_hash").getAsString(),
				input_list, output_list, jsonObject.get("timestamp").getAsLong(), 
				jsonObject.get("signsig").getAsString(), jsonObject.get("coinbase").getAsBoolean());
		return transaction;
	}

}