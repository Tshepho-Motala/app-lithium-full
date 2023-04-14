package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ValidatePreRegistration {
    private String applicantGuid;
    private String email;
    private String cellphoneNumber;
}
