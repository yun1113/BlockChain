import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class WalletDeserializer implements JsonDeserializer<Wallet> {

	@Override
	public Wallet deserialize(JsonElement json, java.lang.reflect.Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {
		
		final JsonObject jsonObject = json.getAsJsonObject();
	    final JsonArray jsonAddressArray = jsonObject.get("address_list").getAsJsonArray();
	    final ArrayList<String> address_list = new ArrayList<String>();
	    for (int i = 0; i < jsonAddressArray.size(); i++) {
	      final String address = jsonAddressArray.get(i).getAsString();
	      address_list.add(address);
	    }
		
		final Wallet wallet = new Wallet(jsonObject.get("account").getAsString());
		wallet.setUUID(jsonObject.get("uuid").getAsString());
		wallet.setAccount(jsonObject.get("account").getAsString());
		wallet.setAddressNum(jsonObject.get("address_num").getAsInt());
		wallet.setAddressList(address_list);
		return wallet;
	}

}