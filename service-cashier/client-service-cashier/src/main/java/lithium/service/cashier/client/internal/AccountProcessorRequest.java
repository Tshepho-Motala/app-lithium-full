package lithium.service.cashier.client.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountProcessorRequest {
    String methodCode;
    private String redirectUrl;
    @Builder.Default
    private Map<String, String> properties = new HashMap<>();
    private DoProcessorRequestUser user;
    private Long accountTransactionId;
    private Long processorId;
    private Map<String, String> metadata;

    public String getProperty(String key) throws Exception {
        if (properties == null) throw new Exception("Processor properties missing");
        if (properties.get(key) == null) throw new Exception("Processor property " + key + " missing");
        return properties.get(key);
    }

}
