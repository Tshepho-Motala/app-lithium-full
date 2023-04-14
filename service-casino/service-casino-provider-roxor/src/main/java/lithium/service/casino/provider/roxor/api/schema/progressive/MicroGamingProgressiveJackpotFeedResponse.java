package lithium.service.casino.provider.roxor.api.schema.progressive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MicroGamingProgressiveJackpotFeedResponse {

    private int progressiveId;
    private int moduleId;
    private int gamePayId;
    private BigDecimal startAtValue;
    private BigDecimal endAtValue;
    private BigDecimal numberOfSeconds;
    private int centsPerSecond;
    private String currencyIsoCode;
    private String friendlyName;
    private int jackpotNumber;
    private int secondsSinceLastWin;
}
