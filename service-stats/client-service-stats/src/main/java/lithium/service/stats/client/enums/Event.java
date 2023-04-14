package lithium.service.stats.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum Event {
	BONUS_HOURLY("bonus.hourly"),
	CLICK("click"),
	LOGIN_SUCCESS("login-success"),
	LOGIN_FAIL("login-fail"),
	REGISTRATION_SUCCESS("registration-success"),
	MANUAL_APPROVED_WITHDRAWAL("manual_approved_withdrawal"),
	AUTO_APPROVED_WITHDRAWAL("auto_approved_withdrawal"),
	KYC_VERIFICATION_ATTEMPT("kyc-verification-attempt");

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String event;

	@JsonCreator
	public static Event fromEvent(String event) {
		for (Event e: Event.values()) {
			if (e.event.equalsIgnoreCase(event)) {
				return e;
			}
		}
		return null;
	}
}
