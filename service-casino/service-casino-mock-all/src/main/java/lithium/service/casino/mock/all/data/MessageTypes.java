package lithium.service.casino.mock.all.data;

public enum MessageTypes {
	LOGIN("login"),
	GET_BALANCE("getbalance"),
	PLAY("play"),
	END_GAME("endgame"),
	REFRESH_TOKEN("refreshtoken"),
	UNKNOWN("unknown");
	
	private String typeName;
	
	MessageTypes(String typeName) {
		this.setTypeName(typeName);
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public static MessageTypes getMessageTypeFromString(final String typeName) {
		for (MessageTypes mt : MessageTypes.values()) {
			if (mt.getTypeName().contentEquals(typeName)) {
				return mt;
			}
		}
		return MessageTypes.UNKNOWN;
	}
}
