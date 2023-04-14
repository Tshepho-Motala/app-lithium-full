package lithium.service.games.client.objects.progressive;

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
public class ProgressiveBalanceGameProgressiveAmountData implements Serializable {
    private BigDecimal amount;
    private String currency;
}
