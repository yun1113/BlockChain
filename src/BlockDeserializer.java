import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class BlockDeserializer implements JsonDeserializer<Block> {

	@Override
	public Block deserialize(JsonElement json, java.lang.reflect.Type arg1, JsonDeserializationContext arg2)
			throws JsonParseException {

		final JsonObject jsonObject = json.getAsJsonObject();

		final JsonArray jsonAddressArray = jsonObject.get("transaction_list").getAsJsonArray();
		final ArrayList<String> transaction_list = new ArrayList<String>();
		for (int i = 0; i < jsonAddressArray.size(); i++) {
			final String transaction = jsonAddressArray.get(i).getAsString();
			transaction_list.add(transaction);
		}

		final Block block = new Block(jsonObject.get("block_hash").getAsString(),
				jsonObject.get("timestamp").getAsLong(), jsonObject.get("nonce").getAsInt(),
				jsonObject.get("prev_block_hash").getAsString(), transaction_list, jsonObject.get("next_block_hash").getAsString());
		return block;
	}

}