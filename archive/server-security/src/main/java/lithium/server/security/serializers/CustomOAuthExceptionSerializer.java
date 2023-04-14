package lithium.server.security.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lithium.server.security.exceptions.CustomOAuthException;

import java.io.IOException;
import java.util.Map;

public class CustomOAuthExceptionSerializer extends StdSerializer<CustomOAuthException> {

	public CustomOAuthExceptionSerializer() {
		super(CustomOAuthException.class);
	}

	@Override
	public void serialize(CustomOAuthException value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
		jsonGenerator.writeStartObject();
		jsonGenerator.writeObjectField("error", value.getOAuth2ErrorCode());
		jsonGenerator.writeObjectField("error_description", value.getMessage());
		if (value.getAdditionalInformation() != null) {
			for (Map.Entry<String, String> entry : value.getAdditionalInformation().entrySet()) {
				String key = entry.getKey();
				String add = entry.getValue();
				jsonGenerator.writeStringField(key, add);
			}
		}
		jsonGenerator.writeEndObject();
	}
}
