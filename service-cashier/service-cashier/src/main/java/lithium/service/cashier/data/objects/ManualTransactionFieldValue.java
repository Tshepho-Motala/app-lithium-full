package lithium.service.cashier.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ManualTransactionFieldValue {
	private String key;
	private String value;
	private int stage;
	private boolean input;
}