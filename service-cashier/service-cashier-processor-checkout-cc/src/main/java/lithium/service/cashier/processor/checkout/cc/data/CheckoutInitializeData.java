package lithium.service.cashier.processor.checkout.cc.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckoutInitializeData {
    private String publicKey;
}
