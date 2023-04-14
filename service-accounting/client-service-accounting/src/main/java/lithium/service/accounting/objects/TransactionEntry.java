package lithium.service.accounting.objects;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long transactionId;

	private Date date;

	private Long amountCents;

	private Account account;
	
	private Long id;
	
	private Long postEntryAccountBalanceCents;
	
	private String transactionType;

}
