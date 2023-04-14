package lithium.service.casino.client.data;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlayerBonusHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Date startedDate;
	private Long playThroughCents;
	private Long playThroughRequiredCents;
	private Long triggerAmount;

	private Long bonusAmount;
	private Integer bonusPercentage;

	private Boolean completed;
	private Boolean cancelled;
	private Boolean expired;

	private BonusRevision bonusRevision;
}
