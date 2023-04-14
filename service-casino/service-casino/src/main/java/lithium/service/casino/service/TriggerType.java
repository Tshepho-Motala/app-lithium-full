package lithium.service.casino.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum TriggerType {
	//0:manual/1:deposit/2:login/3:raf/4:xp/5:reward/6:consecutive/7:product/8:leaderboard/9:hourly
	TRIGGER_MANUAL(0, "manual"),
	TRIGGER_DEPOSIT(1, "deposit"),
	TRIGGER_LOGIN(2, "login"),
	TRIGGER_RAF(3, "raf"),
	TRIGGER_XP(4, "xp"),
	TRIGGER_REWARD(5, "reward"),
	TRIGGER_CONSECUTIVE(6, "consecutive"),
	TRIGGER_PRODUCT(7, "product"),
	TRIGGER_LEADERBOARD(8, "leaderboard"),
	TRIGGER_HOURLY(9, "hourly"),
	TRIGGER_FRONTEND(10, "frontend");
	
	@Getter
	@Setter
	@Accessors(fluent = true)
	private Integer type;
	@Getter
	@Setter
	@Accessors(fluent = true)
	private String trigger;
	
	@JsonCreator
	public static TriggerType fromType(int type) {
		for (TriggerType g : TriggerType.values()) {
			if (g.type == type) {
				return g;
			}
		}
		return TRIGGER_MANUAL;
	}
}