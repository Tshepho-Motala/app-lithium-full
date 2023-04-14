package lithium.service.cashier;

/**
 * @deprecated This has been moved to [library-service] to not have to import client-service-cashier if not needed, replaced by {@link lithium.cashier.CashierTransactionLabels}
 */
@Deprecated
public interface CashierTransactionLabels {
	public static final String TRAN_ID_LABEL = "transaction_id";
	public static final String PROVIDER_GUID_LABEL = "provider_guid";
	public static final String PROCESSING_METHOD_LABEL = "processing_method";
	public static final String DOMAIN_METHOD_PROCESSOR_ID = "domain_method_processor_id";
	public static final String FIRST_DEPOSIT_LABEL = "first_deposit";
	public static final String GEO_COUNTRY_LABEL = "geo_country";
	public static final String GEO_STATE_LABEL = "geo_state";
	public static final String GEO_CITY_LABEL = "geo_city";
	public static final String DEVICE_OS_LABEL = "device_os";
	public static final String DEVICE_BROWSER_LABEL = "device_browser";
	public static final String COMMENT_LABEL = "comment";
	public static final String FEES_FLAT = "fees_flat";
	public static final String FEES_MINIMUM = "fees_minimum";
	public static final String FEES_PERCENTAGE = "fees_percentage";
	public static final String FEES_PERCENTAGE_FEE = "fees_percentage_fee";
	public static final String FEES_PLAYER_AMOUNT = "fees_player_amount";
	public static final String TRAN_ID_REVERSE_LABEL = "reverse_transaction_id";
	public static final String ORIGINAL_TRAN_ID = "original_transaction_id";
	public static final String PROCESSOR_REFERENCE = "processor_reference";
	public static final String ADDITIONAL_REFERENCE = "additional_reference";
	public static final String PLAYER_PAYMENT_METHOD_REFERENCE = "player_payment_method_reference";
}
