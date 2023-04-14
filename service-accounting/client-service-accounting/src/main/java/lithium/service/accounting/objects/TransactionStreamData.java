package lithium.service.accounting.objects;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class TransactionStreamData  implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long transactionId;

	private String transactionType;

	private String ownerGuid;

}
