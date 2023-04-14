package lithium.service.cashier.processor.vespay.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown=true)
public class VoucherRedemptionResponse {
	@JsonProperty("sender")
	private String sender;
	@JsonProperty("apikey")
	private String apiKey;
	@JsonProperty("approved")
	private Boolean approved;
	@JsonProperty("errorcode")
	private Integer errorCode;
	@JsonProperty("errordescription")
	private String errorDescription;
	@JsonProperty("parametererrors")
	private String parameterErrors;
	@JsonProperty("traceid")
	private String traceId;
	@JsonProperty("transactionid")
	private Integer transactionId;
	@JsonProperty("orderid")
	private Integer orderId;
	@JsonProperty("amount")
	private Integer amount;
	@JsonProperty("currency")
	private String currency;
	@JsonProperty("bin")
	private Integer bin;
	@JsonProperty("billingdescriptor")
	private String billingDescriptor;
	@JsonProperty("messageclient")
	private String messageClient;
	@JsonProperty("datetimecreated")
	private String dateTimeCreated;
	@JsonProperty("timezone")
	private String timeZone;
}
