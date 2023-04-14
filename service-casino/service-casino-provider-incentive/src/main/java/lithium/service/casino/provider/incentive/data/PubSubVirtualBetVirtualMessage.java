package lithium.service.casino.provider.incentive.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PubSubVirtualBetVirtualMessage implements PubSubVirtualMessage {
    private String eventType;
    private String accountGuid;
    private long accountId;
    private Double value;
    private String product;
}
