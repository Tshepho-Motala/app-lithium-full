package lithium.service.casino;

/**
 * @deprecated This has been moved to [library-service] to not have to import client-service-casino if not needed, replaced by {@link lithium.casino.CasinoTransactionLabels}
 */
@Deprecated
public interface CasinoTransactionLabels {
	public static final String TRAN_ID_LABEL = "transaction_id";
	public static final String TRAN_ID_REVERSE_LABEL = "reverse_transaction_id";
	public static final String PROVIDER_GUID_LABEL = "provider_guid";
	public static final String GAME_GUID_LABEL = "game_guid";
	public static final String ORIGINAL_TRAN_ID = "original_transaction_id";
	public static final String PLAYER_BONUS_HISTORY_ID = "player_bonus_history_id";
	public static final String BONUS_REVISION_ID = "bonus_revision_id";
	public static final String GAME_SESSION_ID_LABEL = "game_session_id";
	public static final String ACCOUNTING_CLIENT_LABEL = "accounting_client_unique_id";
	public static final String ACCOUNTING_CLIENT_RESPONSE_LABEL = "accounting_client_response_id";
	public static final String BET_ACCOUNTING_TRANSACTION_ID = "bet_acc_tran_id";
	public static final String XP_SCHEME_ID = "xp_scheme_id";
	public static final String PLAYER_BONUS_TOKEN_ID = "player_bonus_token_id";


	String ACCRUAL_ID = "accrual_id";
	String PLATFORM_CODE = "platform_code";
}
