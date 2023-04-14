package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEntryBO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Transaction transaction;
	private Date date;
	private Long amountCents;
	private Account account;
	private Long id;
	private Long postEntryAccountBalanceCents;
	private TransactionEntryBODetails details;
}
