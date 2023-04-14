package lithium.service.cashier.processor.skrill.data;

import lithium.util.FormParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferPrepareRequest {
	/**
	 * Required.
	 * The required action. In the first step, this is ‘prepare’.
	 */
	@FormParam(value="action")
	private String action;
	/**
	 * Required.
	 * Your merchant account email address.
	 */
	@FormParam(value="email")
	private String email;
	/**
	 * Required.
	 * Your MD5 API/MQI password.
	 */
	@FormParam(value="password")
	private String password;
	/**
	 * Required.
	 * Amount to be transferred.
	 */
	@FormParam(value="amount")
	private String amount;
	/**
	 * Required.
	 * Currency
	 */
	private String currency;
	/**
	 * Required.
	 * Recipient’s (beneficiary’s) email address.
	 */
	@FormParam(value="bnf_email")
	private String beneficiaryEmail;
	/**
	 * Required.
	 * Subject of the notification email. Up to 250 1-byte characters.
	 */
	@FormParam(value="subject")
	private String subject;
	/**
	 * Required.
	 * Comment to be included in the notification email. Up to 2000 1-byte characters.
	 */
	@FormParam(value="note")
	private String note;
	/**
	 * Your reference ID (must be unique if submitted).
	 */
	@FormParam(value="frn_trn_id")
	private String referenceId;
}
