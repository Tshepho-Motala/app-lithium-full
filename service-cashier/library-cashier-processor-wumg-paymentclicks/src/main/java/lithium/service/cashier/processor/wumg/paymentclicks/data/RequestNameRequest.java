package lithium.service.cashier.processor.wumg.paymentclicks.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RequestNameRequest {

	private String user;
	private String password;
	private String processor;
	private String sender;
	private String account;
	private String amount;
	
}
