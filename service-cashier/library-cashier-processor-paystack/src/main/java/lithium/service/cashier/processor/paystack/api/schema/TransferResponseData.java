package lithium.service.cashier.processor.paystack.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransferResponseData {
    private String reference;
    private Long integration;
    private String domain;
    private Integer amount;
    private String currency;
    private String source;
    private String reason;
    private Long recipient;
    private String status;
    @JsonProperty("transfer_code")
    private String transferCode;
    private Long id;
    private String createdAt;
    private String updatedAt;
}
