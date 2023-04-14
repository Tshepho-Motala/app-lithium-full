package lithium.service.cashier.data.objects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AutoApproveResult {
    private boolean approved;
    private Long processDelay;
    private String trace;
}
