package lithium.service.access.provider.sphonic.schema.kyc.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String address1;
    private String address2;
    private String city;
    private String county;
    private String postalCode;
    private String country;
}
