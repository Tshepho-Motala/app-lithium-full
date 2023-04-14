package lithium.service.casino.client.objects.progressivejackpotfeed;

import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.casino.client.objects.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressiveJackpotGameBalance implements Serializable {
    private String progressiveId;
    private String currencyCode;
    private BigDecimal amount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal wonByAmount;
    private Game game;
}
