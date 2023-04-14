package lithium.service.cashier.processor.checkout.cc.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class CheckoutCardSourceWebhook {
    String id;
    String type;
    String card_type;
    Object billing_address;
    int expiry_month;
    int expiry_year;
    String scheme;
    String name;
    String last_4;
    String fingerprint;
    String bin;
	String issuerCountry;

}
