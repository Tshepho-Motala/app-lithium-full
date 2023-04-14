package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PostRegistrationSteps {
    public long userId;
    /**
     * Unique user identification in the format of [domainName]/[applicantHash]
     */
    public String applicantGuid;
    public String email;
    public String cellphoneNumber;
}
