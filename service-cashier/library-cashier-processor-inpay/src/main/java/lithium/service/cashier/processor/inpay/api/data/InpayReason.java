package lithium.service.cashier.processor.inpay.api.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InpayReason {
	private String code;
	private String message;
	private String category;
	private Map<String, Object> details;
}
