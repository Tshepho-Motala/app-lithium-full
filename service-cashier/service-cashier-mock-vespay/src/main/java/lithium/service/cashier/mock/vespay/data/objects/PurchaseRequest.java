package lithium.service.cashier.mock.vespay.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseRequest {
	private String apiKey;
	private String traceId;
}
