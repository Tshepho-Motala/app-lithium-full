package lithium.service.access.provider.sphonic.schema.kyc.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdditionalDetails {
    private String documentType;
    private String documentNumber;
    private String birthPlace;
    private String gender;
    private String secondSurname;
    private String telephoneNumber;
    private String uniqueReference;
    private String testRequest;
}
