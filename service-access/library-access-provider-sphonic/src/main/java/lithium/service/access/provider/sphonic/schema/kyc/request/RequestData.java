package lithium.service.access.provider.sphonic.schema.kyc.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestData {
    private Name name;
    private DateOfBirth dateOfBirth;
    private Address address;
    private AdditionalDetails additionalDetails;
}
