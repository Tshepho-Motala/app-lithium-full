package lithium.client.changelog;

import lombok.Getter;

@Getter
public enum SubCategory {
	ACCOUNT("Account", Category.ACCOUNT),
	ACCESS("Access", Category.ACCESS),
	ACCESS_RULE("Access rule", Category.ACCESS),
	SUPPORT("Support", Category.SUPPORT),
	COMMUNICATIONS("Communications", Category.SUPPORT),
	TRANSLATIONS("Translations", Category.SUPPORT),
	RETENTION("Retention", Category.RETENTION),
	BONUSES("Bonuses", Category.BONUSES),
	BONUS_GRANT("Bonus Grant", Category.BONUSES),
	BONUS_CREATE("Bonus Create", Category.BONUSES),
	BONUS_DELETED("Bonus Deleted", Category.BONUSES),
	BONUS_CANCELLED("Cancelled Bonuses", Category.BONUSES),
	SALES("Sales", Category.SALES),
	RESPONSIBLE_GAMING("Responsible Gaming", Category.RESPONSIBLE_GAMING),
	TC_ACCEPTANCE("Terms and Conditions Acceptance", Category.ACCOUNT),
	REALITY_CHECK("Reality Check", Category.FINANCE),
	CLOSURE("Closure", Category.ACCOUNT),
	RESTRICTION("Restriction", Category.ACCOUNT),
	PASSWORD_RESET("Password Reset", Category.SUPPORT),
	PASSWORD_UPDATE("Update Password", Category.SUPPORT),
	CUSTOMER_FUNDS("Customer Funds", Category.ACCOUNT),
	FINANCE("Finance", Category.FINANCE),
	EDIT_DETAILS("Edit Details", Category.SUPPORT),
	KYC("KYC", Category.ACCOUNT),
	STATUS_CHANGE("Status Change", Category.SUPPORT),
	AUTO_WITHDRAW("Auto Withdraw", Category.FINANCE),
	LOSS_LIMITS("Loss Limits",Category.RESPONSIBLE_GAMING),
	FAILED_LOGINS("Excessive Failed Logins", Category.SUPPORT),
	ACCOUNT_CREATION("Account Creation", Category.ACCOUNT),
	DOCUMENT_UPLOAD("Document Upload", Category.ACCOUNT),
	DEPOSIT_LIMITS("Deposit Limits", Category.RESPONSIBLE_GAMING),
	SELF_EXCLUSION("Self-Exclusion", Category.ACCOUNT),
    DOB_CHANGE("Date Of Birth Change", Category.ACCOUNT),
    PLACE_OF_BIRTH_CHANGE("Place Of Birth Change", Category.ACCOUNT),
	TIME_FRAMES_LIMITS("Time Frames Limit", Category.RESPONSIBLE_GAMING),
	GAMSTOP_SELF_EXCLUSION("Gamstop Self-Exclusion", Category.ACCOUNT),
	CRUKS_SELF_EXCLUSION("CRUKS Self-Exclusion", Category.ACCOUNT),
	CRUKS("CRUKS", Category.RESPONSIBLE_GAMING),
	GAMSTOP("GAMSTOP", Category.RESPONSIBLE_GAMING),
	BALANCE_LIMIT("Balance Limit", Category.RESPONSIBLE_GAMING),
	IBAN_MISMATCH("IBAN mismatch", Category.FINANCE),
	PLAY_TIME_LIMIT("Play Time Limit", Category.RESPONSIBLE_GAMING),
	IBAN_CHANGE("IBAN Change", Category.ACCOUNT),
	ECOSYSTEM_SYNCHRONIZATION("Ecosystem Synchronization", Category.ACCOUNT),
	RULE_SET("Rule set", Category.ACCESS),
	DOCUMENT_TYPE("Document Type", Category.DOCUMENTS),
	TEMPLATES("Templates", Category.SUPPORT),
	PROVIDER("Provider", Category.SUPPORT),
	ECOSYSTEM("Ecosystem", Category.SUPPORT),
	AFFILIATE("Affiliate", Category.SUPPORT),
	EDIT_DOMAIN("Edit Domain", Category.SUPPORT),
	ENTITY("Entity", Category.SUPPORT),
	GAMES("Entity", Category.SUPPORT),
	CREATE_DOMAIN("Create Domain", Category.SUPPORT),
	BONUS_REVISION("Bonus Revision", Category.BONUSES),
	BONUS_REGISTER("Register Bonus", Category.BONUSES),
	USER_LINK("User Link", Category.ACCOUNT),
	IDIN_VERIFICATION("iDin Verification", Category.ACCOUNT),
	REWARD_GRANT("Reward Grant", Category.REWARDS),
	REWARD_CANCELLED("Reward Cancelled", Category.REWARDS),
	REWARD_DELETED("Reward Deleted", Category.REWARDS),
	REWARD_CREATED("Reward Created", Category.REWARDS),
	REWARD_DECLINED("Reward Declined", Category.REWARDS),
	PROMOTION_CREATE("Create Promotion", Category.PROMOTIONS),
	PROMOTION_EDIT("Edit Promotion", Category.PROMOTIONS),
	PROMOTION_PARTICIPATION("Promotion Participation", Category.PROMOTIONS);


	private final String name;
	private final Category category;

	SubCategory(String name, Category category) {
		this.name = name;
		this.category = category;
	}

	public static SubCategory fromName(String name) {
		for (SubCategory s: SubCategory.values()) {
			if (s.name.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
}
