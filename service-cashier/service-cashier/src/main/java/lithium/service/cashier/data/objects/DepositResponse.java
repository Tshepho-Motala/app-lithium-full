package lithium.service.cashier.data.objects;

import lombok.Data;

@Data
public class DepositResponse {
	private Long transactionId;
	private Long accountingReference;
	private boolean success;
	private String state;
	private String message;
	private String data;
	private Long depositCount;
}
