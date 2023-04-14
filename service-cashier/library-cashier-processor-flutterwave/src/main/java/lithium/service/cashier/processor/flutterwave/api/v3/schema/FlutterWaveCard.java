package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveCard {
	@JsonProperty("first_6digits")
	String first6Digits;
	@JsonProperty("last_4digits")
	String last4Digits;
	String issuer;
	String country;
	String type;
	String token;
	String expiry;
}
