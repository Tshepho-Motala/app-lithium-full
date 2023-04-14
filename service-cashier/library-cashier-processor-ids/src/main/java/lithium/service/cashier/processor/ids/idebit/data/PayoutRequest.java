package lithium.service.cashier.processor.ids.idebit.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PayoutRequest {

	@JsonProperty(value="merchant_id", required = true)
	private String merchantId;
	@JsonProperty(value="merchant_pass")
	private String perchantPassword;
	@JsonProperty(value="user_id", required = true)
	private String idsUserId;
	@JsonProperty(value="merchant_user_id", required = true)
	private String userGuid;
	@JsonProperty(value="txn_type")
	private String transactionType; //This will be F (payout to customer)
	@JsonProperty(value="merchant_txn_num", required = true)
	private String transactionNumber;
	@JsonProperty(value="txn_amount", required = true)
	private String amountDecimalString;
	@JsonProperty(value="txn_currency", required = true)
	private String currencyCode;
}
