package lithium.service.domain.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Central place to store possible domain setting values.
 *
 * @version 1.0
 * @since   2020-08-12
 */

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum DomainSettings {
	FAILED_PASSWD_RESET_THRESHOLD("failed_passwd_reset_threshold", "20"),
	TERMS_AND_CONDITIONS_VERSION("terms_and_conditions_version", "1"),
	PASSWORD_RESET_TIMEOUT("password_reset_timeout", "60"),
	ALLOW_NEGATIVE_BALANCE_ADJUSTMENT("allow_negative_balance_adjustment", "true"),
	ALLOW_MINIMAL_TOKEN( "allow_minimal_token", "false"),
	ALLOW_DUPLICATED_FULL_NAME_AND_DOB("allow_duplicated_full_name_and_dob", "true"),
	ALLOW_DUPLICATE_EMAIL("allow-duplicate-email","false"),
	ALLOW_VALIDATION_EMAIL("allow-validation-email","false"),
	ALLOW_DUPLICATE_CELLNUMBER("allow-duplicate-cellnumber", "false"),
	DISABLE_SMTP_SENDING("disable_smtp_sending", "false"),
	UPLOADED_DOCUMENT_MAIL_DWH("uploaded_document_mail_dwh", ""),
	PROTECTION_OF_CUSTOMER_FUNDS_ENABLED("protection_of_customer_funds_enabled", "true"),
	PROTECTION_OF_CUSTOMER_FUNDS_VERSION("protection_of_customer_funds_version", "1"),
	PUB_SUB_USER_LINK("pub_sub_user_link", "false"),
	PUB_SUB_CASINO("pub_sub_casino", "false"),
	PUB_SUB_USER_CHANGE("pub_sub_user_change", "false"),
	PUB_SUB_SPORTSBOOK("pub_sub_sportsbook", "false"),
	PUB_SUB_WALLETS("pub_sub_wallets", "false"),
	PUB_SUB_VIRTUALS("pub_sub_virtuals", "false"),
	PUB_SUB_MARKETING_PREFS("pub_sub_marketing_preferences", "false"),
	ALLOW_LOGIN_FROM_UNKNOWN_COUNTRY("allow_login_from_unknown_country", "true"),
	RECENTLY_PLAYED_GAMES_MAX("recently_played_games_max", "12"),
	SUMMARY_ACCOUNT_LV_IGNORED_LABELS("summary_account_lv_ignored_labels", ""),
	PENDING_BALANCE_LIMIT_UPDATE_DELAY("pending_balance_limit_update_delay_in_hr", "168"),
	SESSION_TIMEOUT("Session Timeout", "14400"),
	SESSION_INACTIVITY_TIMEOUT("session_inactivity_timeout", "7200"),
	ACCESS_HASH_PASSWORD("access_hash_password", ""),
	ALLOW_REGISTER_V2("allow_register_v2", "false"),
	ALLOW_REGISTER_V3("allow_register_v3", "false"),
	ALLOW_REGISTER_V4("allow_register_v4", "false"),
	VALIDATE_EMAIL_ON_REGISTRATION("validate_email_on_registration", "false"),
	DEFAULT_DEPOSIT_LIMIT_PLAYER_CONFIRMATION("default-deposit-limit-player-confirmation", "false"),
	DEPOSIT_LIMIT_PENDING_PERIODS_IN_HOURS("default-deposit-limit-pending-periods-in-hr", "24"),
	PENDING_PLAYTIME_LIMIT_UPDATE_DELAY("pending_playtime_limit_update_delay_in_hr", "168"),
	ENABLE_UNDERAGE_REGISTRATION("enable_underage_registration", "false"),
	DISPLAY_LASTNAME_PREFIX("lastNamePrefix", "hide"),
	BANK_ACCOUNT_LOOKUP("bank_account_lookup", "false"),
	CORRELATION_ID("correlation_id", "false"),
	CASINO_ALLOW_TESTACCOUNT_JACKPOT_GAMES("casino_allow_testaccount_jackpot_games", "false"),
	SINGLE_OPT_ALL_CHANNELS("single_opt_all_channels", "false"),
	CANCEL_PENDING_WITHDRAWALS_ON_RESETTLEMENT("cancel_pending_withdrawals_on_resettlement", "true"),
	UPLOAD_DOCUMENT_VERSION("upload_document_version", "v1, v2"),
	SIGNUP_BONUS_CODE("signup_bonus_code", "hide"),
	AGE_ONLY_VERIFIED_STATUS_LEVEL("age_only_verified_status_level", ""),
	OVERRIDE_NEGATIVE_BALANCE_DISPLAY("override_negative_balance_display", "false"),
	PENDING_EMAIL_VALIDATION_ACTIVATE("pending_email_validation_activate", "false"),
	INITIATE_WD_ON_BALANCE_LIMIT_REACHED_DELAY_IN_MS("initiate_wd_on_balance_limit_reached_delay_in_ms", "5000"),
	ALLOW_LIFTING_PLAYER_CASINO_BLOCK("allow_lifting_player_casino_block", "false"),
	MIN_USER_AGE("min_user_age", "18"),
	LEADERBOARD_PUSH_DOMAIN_LINK_OPT_OUT("leaderboard_push_domain_link_opt_out", "hide"),
	ENABLE_ALERT_EMAIL_ON_FAILED_SCHEDULED_REPORT("enable_alert_email_on_failed_scheduled_report", "false"),
	MAXIMUM_BONUS_PAYOUT("maximum_bonus_payout", "125000"),
	SEND_REWARD_NOTIFICATION_TO_PLAYER_INBOX("send_reward_notification_to_player_inbox", "false"),
	RESIDENTIAL_ADDRESS_REQUIRED("residential_address_required", "false"),
	DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED(
			"dangerous_op_migration_sb_open_bets_ingestion_without_balance_adjust_enabled", "false");

	@Getter
	@Setter
	@Accessors(fluent = true)
	private String key;

	@Getter
	@Setter
	@Accessors(fluent = true)
	private String defaultValue;

	@JsonCreator
	public static DomainSettings fromKey(String key) {
		for (DomainSettings ds : DomainSettings.values()) {
			if (ds.key.equalsIgnoreCase(key)) {
				return ds;
			}
		}
		return null;
	}
}
