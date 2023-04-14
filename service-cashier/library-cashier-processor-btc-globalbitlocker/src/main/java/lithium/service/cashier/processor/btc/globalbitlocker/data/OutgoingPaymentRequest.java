package lithium.service.cashier.processor.btc.globalbitlocker.data;

import lithium.service.cashier.processor.btc.globalbitlocker.util.HashCalculator;
import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class OutgoingPaymentRequest {
	public static final String API = "send";
	@FormParam("key")
	private String apiKey;
	private String user;
	@FormParam("callback_url")
	private String callbackUrl;
	private String address;
	private String amount;
	
	public String calculateSign() {
		HashCalculator calc = new HashCalculator(apiKey);
		calc.addItem("key", apiKey);
		calc.addItem("user", user);
		calc.addItem("callback_url", callbackUrl);
		String hash = calc.calculateHash(true); 
		log.debug("Calculating hash using secret: " + apiKey + " " + this.toString() + " hash " + hash);
		return hash;
	}
}