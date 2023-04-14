package lithium.service.access.provider.sphonic.schema.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class AdditionalDetails {
	@JsonProperty("IBAN")
	private String iban;
	private String uniqueReference;
}
