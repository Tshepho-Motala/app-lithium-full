package lithium.service.casino.mock.all.data;

public enum ErrorCodes {
	GENERAL_UNSPECIFIED("6000", "Unspecified Error"),
	GENERAL_PLAYER_TOKEN_INVALID("6001","The player token is invalid."),
	GENERAL_PLAYER_TOKEN_EXPIRED("6002","The player token expired."),
	GENERAL_AUTH_CREDENTIALS_INCORRECT("6003", "The authentication credentials for the API are incorrect"),
	
	LOGIN_VALIDATION_FAILED("6101", "Login validation failed. Login name or password is incorrect"),
	LOGIN_ACCOUNT_LOCKED("6102", "Account is locked."),
	LOGIN_ACCOUNT_DOES_NOT_EXIST("6103", "Account does not exist."),
	LOGIN_PLAYER_SELF_EXCLUDED("6104", "Player is self-excluded."),
	LOGIN_PLAYER_T_AND_C("6105", "Player must accept the T&Cs."),
	LOGIN_PLAYER_PROTECTION("6106", "Must show player protection."),
	LOGIN_IP_RESTRICTED("6107", "The IP address is restricted."),
	LOGIN_PLAYER_PASSWORD_EXPIRED("6108", "The password expired."),
	
	GAMEPLAY_ALREADY_PROCESSED("6500", "Already Processed."),
	GAMEPLAY_PROCESSED_DIFFERENT_DETAILS("6501", "Already processed with different details."),
	GAMEPLAY_INSUFFICIENT_FUNDS("6503", "Player has insufficient funds."),
	GAMEPLAY_EXCEEDED_DAILY_PROTECTION("6505", "The player exceeded their daily protection limit."),
	GAMEPLAY_EXCEEDED_WEEKLY_PROTECTION("6506", "The player exceeded their weekly protection limit."),
	GAMEPLAY_EXCEEDED_MONTHLY_PROTECTION("6507", "The player exceeded their monthly protection limit."),
	GAMEPLAY_EXCEEDED_GAMEPLYA_DURATION("6508", "The player exceeded their game play duration."),
	GAMEPLAY_EXCEEDED_LOSS_LIMIT("6509", "The player exceeded their loss limit."),
	GAMEPLAY_NOT_PERMITTED("6510", "The player is not permitted to play this game."),
	GAMEPLAY_EXTERNAL_SYSYEM_NAME_NOT_EXIST("6511", "The external system name does not exist (gamereference).");
	

	private String code;
	private String description;
	
	ErrorCodes(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
