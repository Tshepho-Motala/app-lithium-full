package lithium.service.user.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Status {
	OPEN("OPEN", "The user may login, but may have other restrictions in place to prevent logging in"
		+ " and/or performing other actions.", true,
		new StatusReason[] {}),
	FROZEN("FROZEN", "The user may not login right now, but in due time the system will automatically"
		+ " change the status to open.", false,
		new StatusReason[] { StatusReason.SELF_EXCLUSION, StatusReason.CRUKS_SELF_EXCLUSION,
				StatusReason.GAMSTOP_SELF_EXCLUSION, StatusReason.COOLING_OFF }),
	BLOCKED("BLOCKED", "The user may not login.", false,
		new StatusReason[] { StatusReason.PLAYER_REQUEST, StatusReason.RESPONSIBLE_GAMING, StatusReason.AML,
				StatusReason.FRAUD, StatusReason.DUPLICATED_ACCOUNT, StatusReason.OTHER,
				StatusReason.ACCESS_RULE, StatusReason.IBAN_MISMATCH }),
	DELETED("DELETED", "The user account has been effectively removed from the system", false,
			new StatusReason[] {});
	@Getter
	@Accessors(fluent=true)
	private String statusName;

	@Getter
	@Accessors(fluent=true)
	private String description;

	@Getter
	@Accessors(fluent=true)
	private boolean userEnabled;

	@Getter
	@Accessors(fluent=true)
	private StatusReason[] possibleReasons;

	@JsonCreator
	public static Status fromName(String name) {
		for (Status s: Status.values()) {
			if (s.statusName.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
}
