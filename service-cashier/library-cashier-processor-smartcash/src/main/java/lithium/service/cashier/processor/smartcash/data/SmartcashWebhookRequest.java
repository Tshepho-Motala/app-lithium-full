package lithium.service.cashier.processor.smartcash.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmartcashWebhookRequest {
    private String merchantId;
}
