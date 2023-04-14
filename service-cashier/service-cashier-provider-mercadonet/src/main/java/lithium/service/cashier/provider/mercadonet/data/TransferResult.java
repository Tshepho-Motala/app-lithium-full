package lithium.service.cashier.provider.mercadonet.data;

import java.util.Date;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferResult {
	private Integer transactionId;
	private Integer methodId;
	private Integer resultCode;
	private Date requestedDate;
}