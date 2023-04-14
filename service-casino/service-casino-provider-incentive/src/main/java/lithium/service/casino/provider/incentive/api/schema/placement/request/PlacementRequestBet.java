
package lithium.service.casino.provider.incentive.api.schema.placement.request;

import java.util.ArrayList;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PlacementRequestBet {

    private long transactionTimestamp;
    private String betTransactionId;
    private double totalOdds;
    private double totalStake;
    private double maxPotentialWin;
    private Long virtualCoinId = null;
    private ArrayList<PlacementRequestEvent> events;

}
