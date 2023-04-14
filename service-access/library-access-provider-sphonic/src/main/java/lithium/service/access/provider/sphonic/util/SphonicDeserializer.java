package lithium.service.access.provider.sphonic.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class SphonicDeserializer extends JsonDeserializer<String> {
	@Override
	public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode node = jp.readValueAsTree();
		if (node.asText().isEmpty()) {
			return null;
		}
		return node.asText();
	}
}
