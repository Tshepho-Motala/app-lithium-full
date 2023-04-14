package lithium.service.casino.client.objects.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties(ignoreUnknown=true)
public class AwardBonusRequest extends Request implements Serializable {
	private static final long serialVersionUID = 1L;
	private String userId;

	private String bankId;

	private Integer rounds;

	private Long roundValueInCents;

	private String games;

	private String comment;

	private String description;

	private String extBonusId;

	private String startTime;

	private String expirationTime;

	private Integer duration;

	private Integer expirationHours;

	private Integer frbTableRoundChips;
	
	private Integer frbBetConfigId;

	private String rewardType;

	private String bonusCode;
}
