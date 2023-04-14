package lithium.service.access.provider.sphonic.schema.iban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = "rawBody")
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IBANResponse {
	@JsonProperty("SphonicResponse")
	private SphonicResponse sphonicResponse;
	private String rawBody;
}
