package lithium.service.kyc.provider.onfido.parser;

import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

public class PropertyParser {
    private final Map<String, Object> properties;
    private final Map<String, String> data;

    private PropertyParser(Map<String, String> data, Map<String, Object> properties) {
        this.data = data;
        this.properties = properties;
    }

    public static PropertyParser of (Map<String, String> data, Map<String, Object> properties) {
        return new PropertyParser(data, properties);
    }

    public String extract(String inputKey, String outputKey) {
        if (properties.containsKey(inputKey)) {
            String property = (String) properties.get(inputKey);
            data.put(outputKey, property);
            return property;
        }
        return null;
    }

    public String extractDocumentNumbers(String outputKey) {
        if (properties.containsKey("document_numbers")) {
            String documentNumber = ((List<Map<String, String>>) properties.get("document_numbers")).stream()
                    .filter(map -> "document_number".equals(map.get("type")))
                    .map(map -> map.get("value"))
                    .findFirst().orElse(null);
            if (nonNull(documentNumber)) {
                data.put(outputKey, documentNumber);
            }
        }
        return null;
    }

}
