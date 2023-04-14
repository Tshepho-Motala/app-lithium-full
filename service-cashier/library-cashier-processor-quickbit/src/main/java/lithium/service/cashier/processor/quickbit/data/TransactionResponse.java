package lithium.service.cashier.processor.quickbit.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.cashier.processor.quickbit.util.HashCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown=true)
@Slf4j
public class TransactionResponse {
	@JsonProperty("status_code")
	private String statusCode;
	@JsonProperty("status_msg")
	private String statusMsg;
	@JsonProperty("redirect_url")
	private String redirectUrl;
	@JsonProperty("request_reference")
	private String requestReference;
	@JsonProperty("checksum")
	private String checksum;
	
	public String calculateHash(String secret) {
		HashCalculator calc = new HashCalculator(secret);
		if (statusCode != null && !statusCode.isEmpty()) calc.addItem("status_code", statusCode);
		if (statusMsg != null && !statusMsg.isEmpty()) calc.addItem("status_msg", statusMsg);
		if (redirectUrl != null && !redirectUrl.isEmpty()) calc.addItem("redirect_url", redirectUrl);
		if (requestReference != null && !requestReference.isEmpty()) calc.addItem("request_reference", requestReference);
		String hash = calc.calculateHash();
		log.info("Calculating hash using secret: " + secret + " " + this.toString() + " hash " + hash);
		return hash;
	}
}
