
package lithium.service.casino.provider.incentive.api.schema.placement.request;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
public class PlacementRequest {

    private String userId;
    private String currencyCode;
    private List<PlacementRequestBet> bets;
    private String extraData;
    private String sha256;

}
