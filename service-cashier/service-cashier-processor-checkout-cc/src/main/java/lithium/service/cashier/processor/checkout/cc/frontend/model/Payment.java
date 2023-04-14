package lithium.service.cashier.processor.checkout.cc.frontend.model;

import lithium.service.cashier.client.objects.UserCard;
import lombok.*;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
	private String publicKey;
	private List<UserCard> userCards;
	private String userFullName;
}
