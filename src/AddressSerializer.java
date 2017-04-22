import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class AddressSerializer implements JsonSerializer<Address> {

	@Override
	public JsonElement serialize(Address address, Type arg1, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();
		result.add("address", new JsonPrimitive(address.getAddress()));
		result.add("wallet_id", new JsonPrimitive(address.getWalletId()));
		result.add("pub_key", new JsonPrimitive(address.getPublicKey()));
		result.add("pri_key", new JsonPrimitive(address.getPrivateKey()));
		result.add("value", new JsonPrimitive(address.getValue()));
		
		JsonArray jsonArray = new JsonArray();
		ArrayList<String> transaction_list = address.getTransactionList();
		if (transaction_list != null) {
			for (String value : transaction_list) {
				jsonArray.add(new JsonPrimitive(value));
			}
			result.add("transaction_list", jsonArray);
		}
		return result;
	}
}