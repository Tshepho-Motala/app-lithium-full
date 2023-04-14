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
/**
 * There is a modify_type mapping table for this class that was added as a key reference to modify_type values saved
 * and used across the limit DB. This was done to provide clarity for data warehouse purposes
 * {Ticket} : https://jira.livescore.com/browse/PLAT-1205
 * {TA} : https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2563473414/WAITING+FOR+REVIEW+LSPLAT-491+PLAT-1205+-+Generate+mapping+tables+for+limits
 */
@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum ModifyType {
	CREATED(0, "CREATED"),
	UPDATED(1, "UPDATED"),
	REMOVED(2, "REMOVED");

	
	@Setter
	@Accessors(fluent=true)
	private Integer id;

	@Getter
	@Setter
	@Accessors(fluent=true)
	private String type;
	
	@JsonValue
	public Integer id() {
		return id;
	}

	public static ModifyType fromType(String type) {
		for (ModifyType t: ModifyType.values()) {
			if (t.type.equalsIgnoreCase(type)) {
				return t;
			}
		}
		return null;
	}
	
	@JsonCreator
	public static ModifyType fromId(Integer id) {
		for (ModifyType t: ModifyType.values()) {
			if (t.id.compareTo(id) == 0) {
				return t;
			}
		}
		return null;
	}
}