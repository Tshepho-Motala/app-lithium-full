package lithium.service.cashier.processor.interswitch.api.schema;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class InterswitchTransactionResponse {
    @JsonProperty("Amount")
    private String amount;
    @JsonProperty("CardNumber")
    private String cardNumber;
    @JsonProperty("MerchantReference")
    private String merchantReference;
    @JsonProperty("PaymentReference")
    private String paymentReference;
    @JsonProperty("RetrievalReferenceNumber")
    private String retrievalReferenceNumber;
    @JsonProperty("SplitAccounts")
    private List<String> splitAccounts;
    @JsonProperty("TransactionDate")
    private String transactionDate;
    @JsonProperty("ResponseCode")
    private String responseCode;
    @JsonProperty("ResponseDescription")
    private String responseDescription;
    @JsonProperty("AccountNumber")
    private String accountNumber;
}
