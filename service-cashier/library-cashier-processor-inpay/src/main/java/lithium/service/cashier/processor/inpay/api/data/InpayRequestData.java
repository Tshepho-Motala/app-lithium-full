package lithium.service.cashier.processor.inpay.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InpayRequestData {
	private String amount;
	@JsonProperty("currency_code")
	private String currencyCode;
	@JsonProperty("end_to_end_id")
	private String endToEndId;

	@JsonProperty("local_instrument")
	private String localInstrument;

	@JsonProperty("remittance_description")
	private String remittanceDescription;

	private InpayParticipant creditor;

	@JsonProperty("creditor_account")
	private InpayAccount creditorAccount;

	@JsonProperty("ultimate_debtor")
	private InpayParticipant ultimateDebtor;

	private InpayDebtor debtor;

	@JsonProperty("debtor_account")
	private InpayDebtorAccount debtorAccount;
}
