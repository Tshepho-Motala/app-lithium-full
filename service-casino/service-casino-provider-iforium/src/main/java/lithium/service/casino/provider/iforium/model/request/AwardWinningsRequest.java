
package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AwardWinningsRequest extends GameRoundRequest {

    @JsonProperty("Amount")
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @JsonProperty("StartRound")
    @NotNull
    private Boolean startRound;

    @JsonProperty("EndRound")
    @NotNull
    private Boolean endRound;

    @JsonProperty("JackpotWinnings")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @DecimalMin(value = "0")
    private BigDecimal jackpotWinnings;
}
