package lithium.service.xp.messagehandlers.api;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IXPMeter {
	@NotNull
	@Default
	private boolean currentLevelIsMilestone = false;
	@NotNull
	private Long currentPoints;
	@NotNull
	private Integer currentLevel;
	@NotNull
	private Long currentLevelPointsRequired;
	
	@NotNull
	private Long nextLevelPointsRequired;
	@NotNull
	@Default
	private boolean nextLevelIsMilestone = false;
}