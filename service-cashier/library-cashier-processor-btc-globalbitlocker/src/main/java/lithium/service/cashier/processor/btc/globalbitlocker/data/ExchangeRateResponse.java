package lithium.service.cashier.processor.btc.globalbitlocker.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.util.FormParam;
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
public class ExchangeRateResponse {
	@FormParam("system_status")
	@JsonProperty("system_status")
	private String systemStatus;  //  System status after the method call, "success" when the method was executed successfully and "fail" when there was a problem.
	@FormParam("system_message")
	@JsonProperty("system_message")
	private String systemMessage;  //  Messages returned by the system when the system status is "fail".
	private String rate;  //  Current Bitcoin exchange rate at Global Bitcoin Locker.
}