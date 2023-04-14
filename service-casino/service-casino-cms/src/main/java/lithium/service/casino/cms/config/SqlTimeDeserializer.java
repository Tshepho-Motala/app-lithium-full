package lithium.service.casino.cms.config;

import java.io.IOException;
import java.sql.Time;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlTimeDeserializer extends JsonDeserializer<Time> {

    @Override
    public Time deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (jp.getValueAsString() != null && jp.getValueAsString().isEmpty()) {
            return null;
        }
        if (jp.getValueAsString() != null && jp.getValueAsString().length() == 5) {
            return Time.valueOf(jp.getValueAsString() + ":00");
        }
        return Time.valueOf(jp.getValueAsString());
    }
}