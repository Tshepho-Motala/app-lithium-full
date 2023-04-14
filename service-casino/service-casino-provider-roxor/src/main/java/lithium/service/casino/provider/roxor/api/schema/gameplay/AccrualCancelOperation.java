package lithium.service.casino.provider.roxor.api.schema.gameplay;

import lithium.service.casino.provider.roxor.api.schema.Accrual;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AccrualCancelOperation extends GamePlayOperation {
    private Accrual amount;
    private String poolId;
    private String accrualId;
    private String reference;
}
