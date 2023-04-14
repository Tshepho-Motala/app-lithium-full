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
public class WithdrawResponse {

	@JsonProperty("SUCCESS")
	private String success;
	
	@JsonProperty("WITHDRAW") 
	private WithdrawData data = new WithdrawData();
	
	@Data
	public static class WithdrawData {
		@JsonProperty("ID")
		private String id;
	}
}
