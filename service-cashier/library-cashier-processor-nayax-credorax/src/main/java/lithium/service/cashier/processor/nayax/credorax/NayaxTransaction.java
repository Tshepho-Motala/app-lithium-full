package lithium.service.cashier.processor.nayax.credorax;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NayaxTransaction {

	Boolean success;
	NayaxTransactionInfo info;
	
}
