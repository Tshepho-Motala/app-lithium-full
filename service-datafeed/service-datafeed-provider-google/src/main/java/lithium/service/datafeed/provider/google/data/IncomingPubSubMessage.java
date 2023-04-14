package lithium.service.datafeed.provider.google.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomingPubSubMessage {
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonProperty("domainName")
    private String domainName;
    @JsonProperty("data")
    private Map<String, Object> data;
    @JsonProperty("dataType")
    private String dataType;
    @JsonProperty("eventType")
    private String eventType;
}
