package lithium.service.casino.api.backoffice.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ActiveBonusResponse {
    private String bonusCode;
    private String bonusName;
    private long playerBonusHistoryId;
    private String provider;
    private Long campaignId;
}
