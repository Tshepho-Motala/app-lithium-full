package lithium.service.cashier.processor.hexopay.api.page.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.hexopay.api.gateway.data.AdditionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Order {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CardOnFile {
        private String initiator;
        private String type;
    }

    Long amount;
    private String currency;
    private String description;
    @JsonProperty("tracking_id")
    private String trackingId;
    @JsonProperty("expired_at")
    private String expiredAt;

    @JsonProperty("additional_data")
    private AdditionData additionalData;

    //TODO: Object avs_cvc_verification;

    @JsonProperty("card_on_file")
    private CardOnFile cardOnFile;



}
