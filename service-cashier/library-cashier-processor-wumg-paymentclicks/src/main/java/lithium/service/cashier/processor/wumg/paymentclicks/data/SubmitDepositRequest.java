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
public class SubmitDepositRequest {

	private String user;
	private String password;
	private String name;
	private String sender_name;
	private String sender_pin;
	private String amount;
	private String control_number;
	private String sender_city;
	private String sender_state;
	private String sender_country;
	private String comments;
	
}
