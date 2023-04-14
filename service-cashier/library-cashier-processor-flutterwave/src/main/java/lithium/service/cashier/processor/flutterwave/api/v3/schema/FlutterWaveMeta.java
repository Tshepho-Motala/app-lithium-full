package lithium.service.cashier.processor.flutterwave.api.v3.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class FlutterWaveMeta {
	@JsonProperty("__CheckoutInitAddress")
	String initAddress;
	String originatoraccountnumber;
	String originatorname;
	String bankname;
	String originatoramount;
}
