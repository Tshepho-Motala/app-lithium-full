package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class Bonus {

	private Integer bonusId;

	private String awardedDate;

	private Integer rounds;

	private Integer roundsLeft;

	private String gameIds;

	private String description;

	private String startTime;

	private String expirationTime;

	private Integer duration;
}