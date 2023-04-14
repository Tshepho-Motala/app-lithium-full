package lithium.service.limit.data.objects;

import lithium.service.limit.client.objects.PlayerLimit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayerBalanceLimit {
    private PlayerLimit current;
    private PlayerLimit pending;
    @Builder.Default
    private Boolean disabled = false;
    private Integer pendingLimitDelay;
}
