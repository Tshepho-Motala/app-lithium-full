package lithium.service.casino.provider.roxor.api.schema.gameplay;

import lithium.service.casino.provider.roxor.api.schema.Money;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TransferOperation extends GamePlayOperation {
    private Money amount;
    private TypeEnum type;
    private String transferId;
}
