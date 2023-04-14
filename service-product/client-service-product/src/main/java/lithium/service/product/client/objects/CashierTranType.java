package lithium.service.product.client.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum CashierTranType {
	PRODUCT_PURCHASE("PRODUCT_PURCHASE"),
	PRODUCT_PAYOUT("PRODUCT_PAYOUT"),
	PLAYER_BALANCE("PLAYER_BALANCE");
	
	@Getter
	@Accessors(fluent = true)
	private String value;
}