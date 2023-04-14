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
public class ReceiveAddressRequest {
	public static final String API = "receive";
	@FormParam("key")
	private String apiKey;
	private String user;
	@FormParam("callback_url")
	private String callbackUrl;
	
	private Long ts;
	private String sign;
	
	public String calculateSign() {
		HashCalculator calc = new HashCalculator(apiKey);
		calc.addItem("key", apiKey);
		calc.addItem("user", user);
		calc.addItem("callback_url", callbackUrl);
		String hash = calc.calculateHash(true); 
		log.info("Calculating hash using secret: " + apiKey + " " + this.toString() + " hash " + hash);
		return hash;
	}
	
	public ReceiveAddressRequest sign() {
//		ts = (long) new Date().getTime() / 1000;
		HashCalculator calc = new HashCalculator(apiKey);
		calc.addItem("key", apiKey);
		calc.addItem("user", user);
		calc.addItem("callback_url", callbackUrl);
		setSign(calc.calculateHash());
		return this;
	}
}