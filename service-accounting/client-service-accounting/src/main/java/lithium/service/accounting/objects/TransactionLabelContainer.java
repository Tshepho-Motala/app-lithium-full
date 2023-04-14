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
public class TransactionLabelContainer implements Serializable {

	private static final long serialVersionUID = 1L;

	private long transactionId;
	
	private List<TransactionLabelBasic> labelList;
}
