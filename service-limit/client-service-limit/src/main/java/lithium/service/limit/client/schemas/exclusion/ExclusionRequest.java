package lithium.service.limit.client.schemas.exclusion;

import lithium.service.limit.client.objects.ExclusionSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExclusionRequest {
	private String playerGuid;
	private Integer periodInMonths; // Permanent exclusion = null

	// Added for Gamstop
	private Date exclusionEndDate;
	private String advisor;
	private ExclusionSource exclusionSource;
}
