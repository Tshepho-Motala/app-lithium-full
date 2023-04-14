package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ExternalProviderRegistration {
    private boolean registrationAllowed;
    private int lastStageCompleted;
}
