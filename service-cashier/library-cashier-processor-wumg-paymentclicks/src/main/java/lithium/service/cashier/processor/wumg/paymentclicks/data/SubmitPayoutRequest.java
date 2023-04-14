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
public class SubmitPayoutRequest {

	private String user;
	private String password;
	private String amount;
	private String processor;
	private String receiver_pin;
	private String receiver_name;
	private String receiver_city;
	private String receiver_state;
	private String receiver_country;
	private String comments;
	
}
