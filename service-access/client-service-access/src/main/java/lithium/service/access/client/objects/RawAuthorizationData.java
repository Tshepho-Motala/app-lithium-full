package lithium.service.access.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Container for raw authorization data sent to the remote provider and received from the remote provider.
 */
public class RawAuthorizationData implements Serializable {
	private String rawRequestToProvider;
	private String rawResponseFromProvider;
}
