package lithium.service.access.provider.sphonic.schema.iban;

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
public class RequestData {
	private Name name;
	private AdditionalDetails additionalDetails;
}
