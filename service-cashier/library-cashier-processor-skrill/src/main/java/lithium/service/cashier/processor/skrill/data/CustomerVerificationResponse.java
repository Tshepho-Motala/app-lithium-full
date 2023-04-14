package lithium.service.cashier.processor.skrill.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerVerificationResponse {
	/**
	 * If the skrill account does not exist, expect 'ACTIVE_CUSTOMER_ACCOUNT_NOT_FOUND'
	 */
	@JsonProperty(value="code")
	private String code;
	@JsonProperty(value="message")
	private String message;
	@JsonProperty(value="email")
	private String email;
	/**
	 * If the customer exists, expect a verificationLevel, otherwise, this is null
	 */
	@JsonProperty(value="verificationLevel")
	private Integer verificationLevel;
}
