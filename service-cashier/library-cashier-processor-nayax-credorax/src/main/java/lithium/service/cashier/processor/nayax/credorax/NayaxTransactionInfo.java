package lithium.service.cashier.processor.nayax.credorax;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NayaxTransactionInfo {
	Long transactionId;
	Long chooseProductTimeout;
	Integer cardType;
	Integer cardEntryMode;
	String cardBin;
	String cardBinHash;
	String propCardUid;
	Integer vmcAuthStatus;
	Integer comStatus;
	String excelData;
	String ccLast4Digits;
}
