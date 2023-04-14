package lithium.service.limit.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum RestrictionEvent {
	REGISTRATION(0, "REGISTRATION"),
	FIRST_DEPOSIT(1, "FIRST_DEPOSIT"),
	SPECIFIC_RESTRICTION_APPLIED(2, "SPECIFIC_RESTRICTION_APPLIED");

	@Setter
	@Accessors(fluent=true)
	private Integer id;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String event;
	
	@JsonValue
	public Integer id() {
		return id;
	}

	public static RestrictionEvent fromEvent(String event) {
		for (RestrictionEvent e : RestrictionEvent.values()) {
			if (event.equalsIgnoreCase(e.event)) {
				return e;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static RestrictionEvent fromId(Integer id) {
		if (id == null) return null;
		for (RestrictionEvent e: RestrictionEvent.values()) {
			if ( e.id.compareTo(id) == 0) {
				return e;
			}
		}
		return null;
	}
}
