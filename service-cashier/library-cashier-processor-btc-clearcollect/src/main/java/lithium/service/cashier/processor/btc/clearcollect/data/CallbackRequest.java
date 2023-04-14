package lithium.service.cashier.processor.btc.clearcollect.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class CallbackRequest {

	@JsonProperty("id")
	private String id;
	
	@JsonProperty("serverdatetime")
	private String serverDateTime;
	
	@JsonProperty("clienttracking")
	private String clientTracking;
	
	@JsonProperty("sender")
	private String sender;
	
	@JsonProperty("securerate")
	private String securerate;
	
	@JsonProperty("btcpercent")
	private String btcpercent;
	
	@JsonProperty("estreceivedamount_int")
	private String estReceivedAmountInt;
	
	@JsonProperty("requestamountusd_int")
	private String requestAmountUsdInt;
	
	@JsonProperty("creditamountusd_int")
	private String creditAmountUsdInt;
	
	@JsonProperty("feeusd_int")
	private String feeUSDInt;
	
	@JsonProperty("requestamountbtc_int")
	private String requestAmountBtcInt;
	
	@JsonProperty("feebtc_int")
	private String feeBtcInt;
	
	@JsonProperty("address")
	private String address;
	
	@JsonProperty("creationdatetime")
	private String creationDateTime;
	
	@JsonProperty("expirationdatetime")
	private String expirationDateTime;
	
	@JsonProperty("minutestoexpire")
	private String minutesToExpire;
	
	@JsonProperty("timestatus")
	private String timeStatus;
	
	@JsonProperty("chargemerchant")
	private String chargeMerchant;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("txid")
	private String txid;
	
	@JsonProperty("confirmations")
	private String confirmations;
	
}
