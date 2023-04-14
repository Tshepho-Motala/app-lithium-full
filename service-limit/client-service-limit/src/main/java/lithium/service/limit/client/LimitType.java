package lithium.service.limit.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum LimitType {
	TYPE_WIN_LIMIT(1),
	TYPE_LOSS_LIMIT(2),
	TYPE_DEPOSIT_LIMIT(3),
	TYPE_DEPOSIT_LIMIT_PENDING(4),
	TYPE_DEPOSIT_LIMIT_SUPPOSED(5),
	TYPE_BALANCE_LIMIT(6),
	TYPE_BALANCE_LIMIT_PENDING(7);

	/**
	 * There is a type mapping table for this field that was added as a key reference to type values saved
	 * in the DB. This was done to provide clarity for data warehouse purposes
	 * {Ticket} : https://jira.livescore.com/browse/PLAT-1205
	 * {TA} : https://playsafe.atlassian.net/wiki/spaces/LITHIUM/pages/2563473414/WAITING+FOR+REVIEW+LSPLAT-491+PLAT-1205+-+Generate+mapping+tables+for+limits
	 */
	@Setter
	@Accessors(fluent = true)
	private Integer type;

	@JsonValue
	public Integer type() {
		return type;
	}

	@JsonCreator
	public static LimitType fromType(int type) {
		for (LimitType lt : LimitType.values()) {
			if (lt.type == type) {
				return lt;
			}
		}
		return null;
	}
}
