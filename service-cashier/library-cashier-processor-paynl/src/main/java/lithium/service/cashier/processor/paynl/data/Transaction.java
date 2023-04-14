package lithium.service.cashier.processor.paynl.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.cashier.processor.paynl.data.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    private String id;
    private String orderId;
    private String type;
    private String serviceId;
    private String description;
    private String reference;
    private String exchangeUrl;
    private Amount amount;
    private String created;
    private String modified;
    private String expire;
    private String refundId;
    private TransactionStatus status;
}
