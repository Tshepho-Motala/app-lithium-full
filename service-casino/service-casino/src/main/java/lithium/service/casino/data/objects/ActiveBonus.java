package lithium.service.casino.data.objects;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActiveBonus {
    private Date grantDate;
    private String domainName;
    private Long bonusId;
    private Long bonusRevisionId;
    private String bonusCode;
    private String bonusName;
    private Long amount;
    private Boolean completed;
    private Boolean expired;
    private Boolean cancelled;
    private String playerGuid;
    private String currencySymbol;

}
