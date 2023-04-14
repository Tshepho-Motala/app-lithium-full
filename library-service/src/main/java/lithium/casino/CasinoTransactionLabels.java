package lithium.casino;

/**
 * This interface is duplicated from the @deprecated {@link lithium.service.casino.CasinoTransactionLabels}
 * Moved here so it's accessible in other services without importing the full client-service-casino.
 */
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
	public static final String LOGIN_EVENT_ID = "login_event_id";
	public static final String PLAYER_REWARD_TYPE_HISTORY_ID = "player_reward_type_history_id"; //TODO: Should this be here?!
	public static final String GAME_PROVIDER_ID = "game_provider_id";
}
