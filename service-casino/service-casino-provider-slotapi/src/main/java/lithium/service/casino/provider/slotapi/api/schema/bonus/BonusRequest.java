package lithium.service.casino.provider.slotapi.api.schema.bonus;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BonusRequest {

    private String playerGuid;
    private Long requestId;
    private Double customAmountDecimal;
    private String bonusCode;
    private String description;
    private String sha256;
}
