package lithium.service.document.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Utils {
    public static String toJson(Object object) {
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("can't parse object to json, " + object.toString() + " , exception " + e.getMessage());
        }
        return json;
    }
}
