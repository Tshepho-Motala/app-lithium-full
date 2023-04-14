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
public class GetWithdrawalResponse {

	@JsonProperty("SUCCESS") 
	private String success;

	@JsonProperty("WITHDRAW") 
	private WithdrawData data = new WithdrawData();
	
	@Data
	public static class WithdrawData {

		@JsonProperty("ID")
		private String id;
		
		@JsonProperty("SERVERDATETIME")
		private String serverDateTime;
		
		@JsonProperty("CLIENTTRACKING")
		private String clientTracking;
		
		@JsonProperty("OUTPUTADDRESS")
		private String outputAddress;
		
		@JsonProperty("SECURERATE")
		private String securerate;
		
		@JsonProperty("BTCPERCENT")
		private String btcpercent;
	
		@JsonProperty("AMOUNTUSD_INT")
		private String amountUsdInt;
		
		@JsonProperty("AMOUNTBTC_INT")
		private String amountBtcInt;
		
		@JsonProperty("DEBITAMOUNTUSD_INT")
		private String debitAmountUsdInt;
		
		@JsonProperty("FEEUSD_INT")
		private String feeUSDInt;
		
		@JsonProperty("DEBITAMOUNTBTC_INT")
		private String debitAmountBtcInt;
		
		@JsonProperty("FEEBTC_INT")
		private String feeBtcInt;
		
		@JsonProperty("CREATIONDATETIME")
		private String creationDateTime;
		
		@JsonProperty("PROCESSDATETIME")
		private String processDateTime;
		
		@JsonProperty("CHARGEMERCHANT")
		private String chargeMerchant;
		
		@JsonProperty("TXID")
		private String txid;
		
		@JsonProperty("STATUS")
		private String status;
	}
	
}
