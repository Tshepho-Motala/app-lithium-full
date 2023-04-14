package lithium.service.cashier.processor.interswitch.api.schema;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class InterswitchBadResponse {
	private InterswitchErrorDescriptor error;
}
