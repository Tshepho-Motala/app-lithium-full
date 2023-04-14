package lithium.service.access.provider.sphonic.schema.iban;

import lithium.service.access.provider.sphonic.schema.RequestDetails;
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
public class IBANRequest {
	private RequestDetails requestDetails;
	private RequestData requestData;
}
