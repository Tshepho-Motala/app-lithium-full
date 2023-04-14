
package lithium.service.casino.provider.incentive.api.schema.placement.response;

import lithium.math.CurrencyAmount;
import lombok.Data;

import java.math.BigDecimal;

@Data
@SuppressWarnings("unused")
public class PlacementResponse {

    private CurrencyAmount balance;
    private long lithiumPlacementId;

}
