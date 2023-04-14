package lithium.service.cashier.processor.checkout.cc.data;

import lombok.*;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionToken {
	private String transactionId;
	private String token;
}
