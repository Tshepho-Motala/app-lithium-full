package lithium.service.cashier.mock.quickbit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuyRequest {
	private String redirectUrl;
	private String requestReference;
	private String fiatAmount;
}
