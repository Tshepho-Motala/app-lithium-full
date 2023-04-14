package lithium.service.casino.provider.roxor.data.response.evolution;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetLimits {

    private String symbol;

    private BigDecimal min;

    private BigDecimal max;
}
