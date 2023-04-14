package lithium.service.casino.provider.roxor.api.schema.gameplay;

import lithium.service.casino.provider.roxor.api.schema.Money;
import lithium.service.casino.provider.roxor.api.schema.Source;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RewardCancelOperation extends GamePlayOperation {
    private Money amount;
    private String transferId;
    private Source source;
}
