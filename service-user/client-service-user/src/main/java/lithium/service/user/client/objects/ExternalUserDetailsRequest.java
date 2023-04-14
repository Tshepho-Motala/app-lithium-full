package lithium.service.user.client.objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalUserDetailsRequest {
    private String apiAuthorizationId;
    private Integer stage;
    private String domainName;
    private IncompleteUserBasic incompleteUserBasic;
}
