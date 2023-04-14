package lithium.service.cashier.processor.checkout.cc.data;


import com.checkout.payments.CardSource;
import com.checkout.payments.CardSourceResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown=true)
public class CheckoutCardVerifyWebhookData {
    CheckoutCardSourceWebhook source;
    Map<String, Object> metadata;
    String reference;
}

