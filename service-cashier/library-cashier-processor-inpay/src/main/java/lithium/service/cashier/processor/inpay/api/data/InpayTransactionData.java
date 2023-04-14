package lithium.service.cashier.processor.inpay.api.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class InpayTransactionData {
	@JsonProperty("debtor_account")
	private InpayDebtorAccount debtorAccount;
	@JsonProperty("end_to_end_id")
	private String endToEndId;
	@JsonProperty("inpay_unique_reference")
	private String inpayUniqueReference;
	private String amount;
	private String currency;
	private String timestamp;
	private String state;
	private List<InpayReason> reasons;
}
