package lithium.service.cashier.processor.btc.clearcollect.util;
import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class PayloadUtil {
	
	public static String createPayloadFromObject(Object o) throws JsonProcessingException {
		String result = "";
		ObjectMapper jsonMapper = new ObjectMapper();
		result = jsonMapper.writeValueAsString(o);
		log.info("createPayloadFromObject " + result);
		result = Base64.getEncoder().encodeToString(result.getBytes());
		return result;
	}
	
	public static <T> T readObjectFromPayload(String payload, Class<T> targetType) throws JsonParseException, JsonMappingException, IOException {
		T o = null;
		byte[] objectAsJson = Base64.getDecoder().decode(payload);
		ObjectMapper json = new ObjectMapper();
		log.info("readObjectFromPayload " + objectAsJson);
		o = (T) json.readValue(objectAsJson, targetType);	
		return o;
	}
}
