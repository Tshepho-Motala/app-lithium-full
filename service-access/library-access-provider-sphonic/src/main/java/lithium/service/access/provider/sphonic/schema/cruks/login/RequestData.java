package lithium.service.access.provider.sphonic.schema.cruks.login;

import lithium.service.access.provider.sphonic.schema.cruks.registration.DateOfBirth;
import lithium.service.access.provider.sphonic.schema.iban.Name;
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
	private AdditionalDetails additionalDetails;
}
