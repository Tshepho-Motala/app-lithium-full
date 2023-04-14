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
public class ExpressPayRequest {

	private String sendercity;
	private String senderstate;
	private String sendercountryId;
	private String senderaddress;
	private String amount;
	private String comments;
	private String sendername;
	private String companyId;
	private String trackingNumber;
	private String player;
	private String receiverId;
	private String extTransId;
	private String tempCommitedId;
	private String moneyChange;
	private String userName;
	private String password;
	private String email;
	private String phone;
	
}
