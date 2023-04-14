package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifyResponseData {
    private Long integration;
    private Recipient recipient;
    private String domain;
    private BigDecimal amount;
    private String currency;
    private String reference;
    private String source;
    @JsonProperty("source_details")
    private String sourceDetails;
    private String reason;
    private String status;
    private String failures;
    @JsonProperty("transfer_code")
    private String transferCode;
    @JsonProperty("titan_code")
    private String titanCode;
    @JsonProperty("transferred_at")
    private String transferredAt;
    private Long id;
    private String createdAt;
    private String updatedAt;
}
