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
public enum StatusReason {
	SELF_EXCLUSION("SELF_EXCLUSION", "Self-Exclusion"),
	CRUKS_SELF_EXCLUSION("CRUKS_SELF_EXCLUSION", "CRUKS Self-Exclusion"),
	GAMSTOP_SELF_EXCLUSION("GAMSTOP_SELF_EXCLUSION", "Gamstop Self-Exclusion"),
	COOLING_OFF("COOLING_OFF", "Cooling Off"),
	PLAYER_REQUEST("PLAYER_REQUEST", "Player Request"),
	RESPONSIBLE_GAMING("RESPONSIBLE_GAMING", "Responsible Gaming"),
	AML("AML", "Anti Money Laundering"),
	FRAUD("FRAUD", "Fraud"),
	DUPLICATED_ACCOUNT("DUPLICATED_ACCOUNT", "Duplicated Account"),
	OTHER("OTHER", "Other"),
	ACCESS_RULE("ACCESS_RULE", "Access Rule"),
	IBAN_MISMATCH("IBAN_MISMATCH", "IBAN Mismatch"),
	DEPOSIT_MISMATCH("DEPOSIT_MISMATCH", "Deposit Mismatch");

	@Getter
	@Accessors(fluent=true)
	private String statusReasonName;

	@Getter
	@Accessors(fluent=true)
	private String description;

	@JsonCreator
	public static StatusReason fromName(String name) {
		for (StatusReason s: StatusReason.values()) {
			if (s.statusReasonName.equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}
}
