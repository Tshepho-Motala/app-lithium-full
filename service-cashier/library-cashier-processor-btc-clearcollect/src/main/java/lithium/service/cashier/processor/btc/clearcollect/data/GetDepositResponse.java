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
public class GetDepositResponse {

	@JsonProperty("SUCCESS")
	private String success;

	
	@JsonProperty("DEPOSIT")
	private DepositData deposit = new DepositData();
	
	@Data
	public static class DepositData {
		
		@JsonProperty("ID")
		private String id;
		
		@JsonProperty("SERVERDATETIME")
		private String serverDateTime;
		
		@JsonProperty("CLIENTTRACKING")
		private String clientTracking;
		
		@JsonProperty("SENDER")
		private String sender;
		
		@JsonProperty("SECURERATE")
		private String securerate;
		
		@JsonProperty("BTCPERCENT")
		private String btcpercent;
		
		@JsonProperty("RECEIVEDAMOUNTBTC_INT")
		private String receivedAmountBtcInt;
		
		@JsonProperty("ESTRECEIVEDAMOUNT_INT")
		private String estReceivedAmountInt;
		
		@JsonProperty("REQUESTAMOUNTUSD_INT")
		private String requestAmountUsdInt;
		
		@JsonProperty("CREDITAMOUNTBTC_INT")
		private String creditAmountBtcInt;
		
		@JsonProperty("CREDITAMOUNTUSD_INT")
		private String creditAmountUsdInt;
		
		@JsonProperty("FEEUSD_INT")
		private String feeUSDInt;
		
		@JsonProperty("REQUESTAMOUNTBTC_INT")
		private String requestAmountBtcInt;
		
		@JsonProperty("FEEBTC_INT")
		private String feeBtcInt;
		
		@JsonProperty("ADDRESS")
		private String address;
		
		@JsonProperty("CREATIONDATETIME")
		private String creationDateTime;
		
		@JsonProperty("EXPIRATIONDATETIME")
		private String expirationDateTime;
		
		@JsonProperty("MINUTESTOEXPIRE")
		private String minutesToExpire;
		
		@JsonProperty("TIMESTATUS")
		private String timeStatus;
		
		@JsonProperty("CHARGEMERCHANT")
		private String chargeMerchant;
		
		@JsonProperty("STATUS")
		private String status;
		
		@JsonProperty("BITTRANS_OUTPUTTXID")
		private String txid;
		
		@JsonProperty("CONFIRMATIONS")
		private String confirmations;
	}
}
