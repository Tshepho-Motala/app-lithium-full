package lithium.service.cashier.processor.smartcash;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmartcashCallbackTransaction {
        private String code;
        @JsonProperty("id")
        private String referenceId;
        private String message;
        private String status_code;
        @JsonProperty("airtel_money_id")
        private String airtelMoneyId;
        @JsonProperty("merchant_request_id")
        private String transactionId;
}
