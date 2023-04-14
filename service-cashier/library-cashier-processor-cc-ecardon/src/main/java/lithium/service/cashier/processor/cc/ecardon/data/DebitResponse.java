package lithium.service.cashier.processor.cc.ecardon.data;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lithium.service.cashier.processor.cc.ecardon.data.enums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DebitResponse {
	private String id;
	private String paymentType;
	private String paymentBrand;
	private String amount;
	private String currency;
	private String descriptor;
	private ResultCode result;
	private Map<String, String> resultDetails;
	private Redirect redirect;
	private Card card;
	private Risk risk;
	private String buildNumber;
	private String timestamp;
	private String ndc;
	private String merchantTransactionId;
}