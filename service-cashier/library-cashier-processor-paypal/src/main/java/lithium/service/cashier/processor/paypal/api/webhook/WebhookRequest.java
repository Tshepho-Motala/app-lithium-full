package lithium.service.cashier.processor.paypal.api.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paypal.api.Payer;
import lithium.service.cashier.processor.paypal.api.Amount;
import lithium.service.cashier.processor.paypal.api.Link;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class WebhookRequest {

    private String id;
    @JsonProperty("event_version")
    private String eventVersion;
    @JsonProperty("create_time")
    private String createTime;
    @JsonProperty("resource_type")
    private String resourceType;
    @JsonProperty("resource_version")
    private String resourceVersion;
    @JsonProperty("event_type")
    private String eventType;
    private String summary;
    private Object resource;
    private List<Link> links;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class PurchaseUnit {
        @JsonProperty("reference_id")
        private String referenceId;
        private Amount amount;
        private Payee payee;
        private String description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Payee {
        @JsonProperty("email_address")
        private String emailAddress;
        @JsonProperty("merchant_id")
        private String merchantId;
        @JsonProperty("display_data")
        private Object displayData;

    }

}
