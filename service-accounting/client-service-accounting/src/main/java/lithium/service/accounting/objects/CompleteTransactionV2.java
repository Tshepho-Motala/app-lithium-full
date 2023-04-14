package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CompleteTransactionV2 implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long transactionId;
	private String transactionType;
	private String createdOn;
	private List<TransactionEntry> transactionEntryList;
	private List<TransactionLabelBasic> transactionLabelList;
	private boolean testUser;
}
