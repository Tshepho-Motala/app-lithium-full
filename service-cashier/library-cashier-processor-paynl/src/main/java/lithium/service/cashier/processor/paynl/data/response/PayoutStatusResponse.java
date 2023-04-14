package lithium.service.cashier.processor.paynl.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.service.cashier.processor.paynl.data.Amount;
import lithium.service.cashier.processor.paynl.data.Customer;
import lithium.service.cashier.processor.paynl.data.Stats;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayoutStatusResponse {
    private String id;
    private String serviceId;
    private String description;
    private String reference;
    private String orderId;
    private String ipAddress;
    private String exchangeUrl;
    private String returnUrl;
    private String paymentUrl;
    private Amount amount;
    private String expire;
    private String created;
    private String modified;
    private Amount amountPaid;
    private Amount amountConverted;
    private Amount amountRefunded;
    private Status status;
    private PaymentMethod paymentMethod;
    private Integration integration;
    private Customer customer;
    private Order order;
    private Stats stats;
    private List<String> transfersData;
    @JsonProperty("_links")
    private List<Links> links;
}
