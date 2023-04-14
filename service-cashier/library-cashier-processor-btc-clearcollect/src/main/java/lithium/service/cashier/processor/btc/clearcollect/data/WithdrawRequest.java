package lithium.service.cashier.processor.btc.clearcollect.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class WithdrawRequest {
	private String nonce;
	
	@JsonProperty("amountusd_int")
	private String amountUsdInt;
	
	@JsonProperty("amountbtc_int")
	private String amountBtcInt;
	
	private String beneficiary;
	
	@JsonProperty("clienttracking")
	private String clientTracking;
	
	@JsonProperty("outputaddress")
	private String outputAddress;
	
	@JsonProperty("authorize")
	private String authorize;
}
