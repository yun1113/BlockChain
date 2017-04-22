import java.lang.reflect.Type;
import java.util.ArrayList;

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
		return result;
	}
}