package lithium.service.cashier.processor.paynl.data.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.paynl.data.Customer;
import lithium.service.cashier.processor.paynl.data.Stats;
import lithium.service.cashier.processor.paynl.data.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayoutRequest {
    private Transaction transaction;
    private Payment payment;
    private Customer customer;
    private Stats stats;
}
