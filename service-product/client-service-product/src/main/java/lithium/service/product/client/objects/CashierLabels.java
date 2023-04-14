package lithium.service.product.client.objects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum CashierLabels {
	TRAN_ID_LABEL ("transaction_id"),
	PROVIDER_GUID_LABEL ("provider_guid"),
	PROCESSING_METHOD_LABEL ("processing_method"),
	DOMAIN_METHOD_PROCESSOR_ID ("domain_method_processor_id"),
	PRODUCT_ID ("product_id"),
	CASHIER_TRAN_ID ("cashier_transaction_id"),
	PAYOUT_ID("payout_id");
	
	@Getter
	@Accessors(fluent = true)
	private String value;
}