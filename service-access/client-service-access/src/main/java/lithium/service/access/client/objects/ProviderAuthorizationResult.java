package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Holds the provider authorization raw outcome as a json string
 * along with information relevant for service access to interpret the outcome from the provider.
 */
public class ProviderAuthorizationResult implements Serializable {
	private EAuthorizationOutcome authorisationOutcome;
	private String errorMessage;
	private ArrayList<RawAuthorizationData> rawDataList;
	private Map<String, String> data;
}
