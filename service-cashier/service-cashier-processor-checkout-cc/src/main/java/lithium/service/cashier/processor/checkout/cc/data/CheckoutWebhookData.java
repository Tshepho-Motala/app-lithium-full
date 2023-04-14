package lithium.service.cashier.processor.checkout.cc.data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class CheckoutWebhookData {
    String response_code;
    String response_summary;
    String amount;
    String id;
    String reference;
    CheckoutCardSourceWebhook source;
}

