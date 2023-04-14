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
public class WithdrawAuthorizeResponse {
	
	@JsonProperty("SUCCESS")
	private String success;
	
}
