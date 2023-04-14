package lithium.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.io.StringWriter;
import java.util.StringJoiner;

public final class ObjectToFormattedText {

    private static ObjectMapper objectMapper = objectMapper();

    private ObjectToFormattedText() {}
    
    private static ObjectMapper objectMapper() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }

    public static String objectToPrettyString(Object o) {
        try {
            StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, o);
            return o.getClass().getSimpleName() + ": " + sw;
        } catch (Exception ex) {
            return "{ 'error': '" + ex.getMessage() + "' }";
        }
    }
    
    public static String jsonObjectToPrettyString(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            StringWriter sw = new StringWriter();
            objectMapper.writeValue(sw, jsonNode);
            return sw.toString();
        } catch (Exception ex) {
            return "{ 'error': '" + ex.getMessage() + "' }";
        }
    }

    public static String httpEntityToPrettyString(HttpEntity entity) {
        StringJoiner res = new StringJoiner("\r\n");
        if (entity instanceof ResponseEntity<?>) {
            res.add("Status: " + ((ResponseEntity<?>) entity).getStatusCodeValue());
        }
        res.add("Body: " + jsonObjectToPrettyString(String.valueOf(entity.getBody())));
        res.add(objectToPrettyString(entity.getHeaders()));
        return res.toString();
    }
}
