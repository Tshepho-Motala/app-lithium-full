package lithium.service.casino.api.frontend.schema;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class BonusHistoryResponse {

    private Date startedDate;
    private Integer bonusType;
    private Integer bonusTriggerType;
    private String bonusCode;
    private String bonusName;
    private String description;
    private Long amountCents;
    private Long requestId;
    private Long playerBonusHistoryId;
}
