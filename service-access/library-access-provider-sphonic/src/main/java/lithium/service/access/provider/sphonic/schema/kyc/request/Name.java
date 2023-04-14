package lithium.service.access.provider.sphonic.schema.kyc.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Name {
    String firstNames;
    String surname;
    String surnamePreFix;
}
