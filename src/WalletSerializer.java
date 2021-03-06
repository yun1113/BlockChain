import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class WalletSerializer implements JsonSerializer<Wallet> {

	@Override
	public JsonElement serialize(Wallet wallet, Type arg1, JsonSerializationContext arg2) {
		JsonObject result = new JsonObject();
		result.add("uuid", new JsonPrimitive(wallet.getUUID()));
		result.add("address_num", new JsonPrimitive(wallet.getAddressNum()));
		result.add("account", new JsonPrimitive(wallet.getAccount()));

		JsonArray jsonArray = new JsonArray();
		ArrayList<String> address_list = wallet.getAddresses();
		if (address_list != null) {
			for (String value : address_list) {
				jsonArray.add(new JsonPrimitive(value));
			}
			result.add("address_list", jsonArray);
		}

		return result;
	}
}