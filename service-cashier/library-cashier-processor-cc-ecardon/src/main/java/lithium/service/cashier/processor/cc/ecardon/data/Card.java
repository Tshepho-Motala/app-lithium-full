package lithium.service.cashier.processor.cc.ecardon.data;

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
public class Card {
	private String bin;
	private String last4Digits;
	private String holder;
	private String expiryMonth;
	private String expiryYear;
}
