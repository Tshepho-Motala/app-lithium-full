package lithium.service.access.provider.sphonic.schema.cruks.registration;

import lithium.service.access.provider.sphonic.schema.cruks.registration.Name;
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
	private DateOfBirth dateOfBirth;
	private AdditionalDetails additionalDetails;
}
