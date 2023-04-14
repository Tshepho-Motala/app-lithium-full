package lithium.service.reward.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AcceptPendingRewardFE {

    @NotNull
    private Long id;
    private boolean accept;
}
