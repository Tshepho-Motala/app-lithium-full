package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VoidBetRequest extends GameRoundRequest {

    @JsonProperty("GameVersion")
    @Size(max = 100)
    private String gameVersion;

    @JsonProperty("TableID")
    @Size(max = 50)
    private String tableId;

    @JsonProperty("Amount")
    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @JsonProperty("JackpotContribution")
    private BigDecimal jackpotContribution;

    @JsonProperty("EndRound")
    @NotNull
    private Boolean endRound;
}
