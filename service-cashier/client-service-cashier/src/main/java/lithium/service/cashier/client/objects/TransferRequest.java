package lithium.service.cashier.client.objects;

import java.io.Serializable;

import org.joda.time.DateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferRequest implements Serializable {

	private static final long serialVersionUID = 3385817234692735679L;
	private Long transferCents;
	private String transactionType; //Deposit or Payout
	private String transactionMethod; //VISA/MC/Envoy/Moneybookers
	private String cardType; //VISA/AMEx/Mastercard
	private String accountNumber; //card number etc
	private DateTime transactionDate;
	private Integer transactionId;
	private String transactionNote;
	private String ipAddress;
	private Long bonusAmount;
	private String currency;
	
	private String userName;
	private String domainName;
	private String providerGuid;
}