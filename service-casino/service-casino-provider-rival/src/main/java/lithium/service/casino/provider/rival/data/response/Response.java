package lithium.service.casino.provider.rival.data.response;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Response {
	private String balance; //This value is a float representing the currency (why could they not just use a cent value?)
	private String currency;
	private String error;
	
	
	//Convert balance to float string from cent value
	public void setBalance(Long balanceCents) {
		BigDecimal bd = new BigDecimal(balanceCents);
		bd = bd.movePointLeft(2);
		balance = bd.toPlainString();
	}
}