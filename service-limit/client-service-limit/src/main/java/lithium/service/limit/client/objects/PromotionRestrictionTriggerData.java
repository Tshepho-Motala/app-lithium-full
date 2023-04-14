package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionRestrictionTriggerData {
    private String userGuid;
    private boolean restrict;
    private Long domainRestrictionSetId;

    // FIXME: WORKAROUND - LSPLAT-5345 - Timing issue on triggering of external bonus restict API (/restrictBonus) on registration; forcing
    // the external restrict bonus call for the second time during the registration flow
    private boolean forceExternalRestrictOnly; //When set to true, then we don't need to set the domainRestrictionSetId
}
