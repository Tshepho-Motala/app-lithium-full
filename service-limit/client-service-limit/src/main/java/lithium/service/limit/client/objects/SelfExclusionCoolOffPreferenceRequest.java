package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelfExclusionCoolOffPreferenceRequest {
    private Long customerId;
    private String lithiumUserGuid;
    private Date requestedDate;
    private boolean currentlyActive;
    private Date startDate;
    private Long periodInDays; // -1 = permanent
    private boolean permanent;
    @Builder.Default
    private boolean coolOffRequest = false;
    @Builder.Default
    private boolean selfExclusionRequest = false;
}
