package lithium.service.access.provider.sphonic.schema.kyc.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateOfBirth {
    private String dataOfBirth;
}
