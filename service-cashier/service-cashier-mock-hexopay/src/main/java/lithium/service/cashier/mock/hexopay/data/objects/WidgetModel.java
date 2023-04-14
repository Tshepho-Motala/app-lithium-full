package lithium.service.cashier.mock.hexopay.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WidgetModel {
	private String token;
	private String amount;
	private String currency;
	private String returnUrl;
}
