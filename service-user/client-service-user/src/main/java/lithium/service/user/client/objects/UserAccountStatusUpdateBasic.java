package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountStatusUpdateBasic {
    private String userGuid;
    private String statusName;
    private String statusReasonName;
    @Builder.Default
    private boolean markSelfExcluded = false;
    @Builder.Default
    private boolean optOutComms = false;
}
