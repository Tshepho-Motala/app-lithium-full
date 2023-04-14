package lithium.service.accounting.objects;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CompleteTransaction implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long transactionId;
	private String transactionType;
	private String createdOn;
	private List<TransactionEntry> transactionEntryList;
	private List<TransactionLabelBasic> transactionLabelList;
}
