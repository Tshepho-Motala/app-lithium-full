package lithium.service.limit.client.objects;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PlayerLimitSummaryFE {
    private List<PlayerLimitFE> balanceLimits;
    private List<PlayerLimitFE> depositLimits;
    private PlayerTimeSlotLimitResponse timeSlotLimit;
    private List<PlayTimeLimitFE> playTimeLimits;
}
