package lithium.service.cashier.processor.flutterwave.api.v2.schema;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveWebhookRequestV2 {
    Long id;
    String txRef;
    String status;
    @JsonProperty("event.type")
    String eventType;
    FlutterWaveWebhookTransfer transfer;
}
