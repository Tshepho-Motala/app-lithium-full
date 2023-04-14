package lithium.service.access.provider.sphonic.schema.cruks.login;

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
	@JsonProperty("CRUKSid")
	private String cruksId;
	private String uniqueReference;
}
