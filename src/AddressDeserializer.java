import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class AddressDeserializer implements JsonDeserializer<Address> {

	@Override
	public Address deserialize(JsonElement json, java.lang.reflect.Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		
		final JsonObject jsonObject = json.getAsJsonObject();
		
		final JsonArray jsonAddressArray = jsonObject.get("transaction_list").getAsJsonArray();
	    final ArrayList<String> transaction_list = new ArrayList<String>();
	    for (int i = 0; i < jsonAddressArray.size(); i++) {
	      final String transaction = jsonAddressArray.get(i).getAsString();
	      transaction_list.add(transaction);
	    }
		
		final Address address = new Address(jsonObject.get("wallet_id").getAsString());
		address.setAddress(jsonObject.get("address").getAsString());
		address.setPublicKey(jsonObject.get("pub_key").getAsString());
		address.setPrivateKey(jsonObject.get("pri_key").getAsString());
		address.setValue(jsonObject.get("value").getAsInt());
		address.setTransactionList(transaction_list);
		return address;
	}

}