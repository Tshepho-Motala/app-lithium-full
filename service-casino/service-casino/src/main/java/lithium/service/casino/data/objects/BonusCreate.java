package lithium.service.casino.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BonusCreate {
	private String bonusCode;
	private String bonusName;
	private Integer bonusType;
	private Integer bonusTriggerType; //0:manual/1:deposit/2:login/3:raf
	private Boolean triggerTypeAny; //meant for any deposit, not a specific #
	private Long triggerAmount; //deposit/login # ?
	private Integer triggerGranularity; //1:year/2:month/3:day/4:week/5:total
	private String domainName;
	private String createdBy;
}