package lithium.service.cashier.processor.btc.clearcollect.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.cashier.processor.btc.clearcollect.util.ValidationException;
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
public class DepositRequest {

	private String nonce;

	@JsonProperty("amountusd_int")
	private String amountUsdCents;
	
	@JsonProperty("amountbtc_int")
	private String amountBtcSatoshis;
	
	private String sender;
	private String clienttracking;
	
	public void validate(String md5Key) throws ValidationException {
		
//		if ((resType != null) && (ResponseType.valueOf(resType) != ResponseType.JSON))  throw new ValidationException(ErrorCode.I0000002);		
		
	}

}
