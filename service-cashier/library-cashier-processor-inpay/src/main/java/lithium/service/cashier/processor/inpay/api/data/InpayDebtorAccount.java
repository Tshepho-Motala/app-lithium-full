package lithium.service.cashier.processor.inpay.api.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InpayDebtorAccount {
	@JsonProperty("scheme_name")
	private String schemeName;
	private Long id;
}
