package lithium.service.cashier.processor.neosurf.data;

import lithium.service.cashier.processor.neosurf.util.HashCalculator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
	private String transactionId;
	private int amount;
	private String created;
	private String merchantTransactionId;
	private String currency;
	private String errorCode;
	private String errorMessage;
	private String status;
	private String methodExpiry;
	private String methodId;
	private String methodLabel;
	private String methodName;
	private String methodChargedAmount;
	private String methodCurrency;
	private String hash;
	private String transaction3d;
	private String subMerchantId;
	private String merchantId;
	private String checksum;

	public String validate(String key) {
		HashCalculator hashCalc = new HashCalculator(key);		
 			hashCalc.addItem("amount", String.valueOf(this.amount));
 			hashCalc.addItem("created", this.created);
 			hashCalc.addItem("currency", this.currency);
 			hashCalc.addItem("errorCode", this.errorCode);
 			hashCalc.addItem("errorMessage", this.errorMessage);
 			hashCalc.addItem("merchantId", this.merchantId);
 			hashCalc.addItem("merchantTransactionId", this.merchantTransactionId);
 			hashCalc.addItem("methodChargedAmount", this.methodChargedAmount);
 			hashCalc.addItem("methodCurrency", this.methodCurrency);
 			hashCalc.addItem("methodExpiry", this.methodExpiry);
 			hashCalc.addItem("methodId", this.methodId);
 			hashCalc.addItem("methodLabel", this.methodLabel);
 			hashCalc.addItem("methodName", this.methodName);
 			hashCalc.addItem("status", this.status);
 			hashCalc.addItem("subMerchantId", this.subMerchantId);
 			hashCalc.addItem("transaction3d", this.transaction3d);
			hashCalc.addItem("transactionId", this.transactionId);
		return hashCalc.calculateHash();

	}
}