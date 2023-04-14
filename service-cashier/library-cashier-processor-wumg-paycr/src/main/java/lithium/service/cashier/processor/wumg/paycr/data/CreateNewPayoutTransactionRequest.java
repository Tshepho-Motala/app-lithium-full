package lithium.service.cashier.processor.wumg.paycr.data;

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
public class CreateNewPayoutTransactionRequest {

	private String receiver;
	private String city;
	private String state;
	private String countryId;
	private String player;
	private String amount;
	private String comments;
	private String companyId;
	private String userName;
	private String password;
	private String docTypeId;
	
}
