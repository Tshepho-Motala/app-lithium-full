package lithium.service.leaderboard.client;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lithium.service.leaderboard.client.objects.Granularity;

import java.io.IOException;

public class GranularityDeserializer extends JsonDeserializer<Granularity> {

	@Override
	public Granularity deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		if (node == null) {
			return null;
		}

		int asInt = node.asInt(-1);

		if (asInt == -1) {
			return null;
		}

		return Granularity.fromGranularity(asInt);
	}
}
