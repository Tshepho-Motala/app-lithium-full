package lithium.service.access.provider.sphonic.schema.cruks.registration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SphonicResponse {
	private lithium.service.access.provider.sphonic.schema.cruks.registration.Data data;
}
