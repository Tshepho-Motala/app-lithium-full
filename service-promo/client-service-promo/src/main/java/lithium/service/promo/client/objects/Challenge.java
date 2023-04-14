package lithium.service.promo.client.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.Valid;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Challenge implements Serializable {
	@Serial
	private static final long serialVersionUID = -215519632946070024L;
	private Long id;
	private int version;
	private String description;
	private Graphic icon;
	private GraphicBasic image;
	private Reward reward;
	private Integer sequenceNumber;

	@Valid
	private ChallengeGroup challengeGroup;
	private List<Rule> rules;

	@Builder.Default
	private Boolean requiresAllRules = Boolean.FALSE;
}
