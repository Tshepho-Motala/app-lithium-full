package lithium.service.document.provider.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDetailsRequest {
    private String cellPhoneNumber;
    private String domainName;
    private String notifyUrl;
}
