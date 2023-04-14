package lithium.service.cashier.processor.paypal.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class OrderConfirmResponse {
    private String status;
    private String errorMessage;
}
