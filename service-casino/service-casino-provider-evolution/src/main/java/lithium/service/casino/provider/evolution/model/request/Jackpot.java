package lithium.service.casino.provider.evolution.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Jackpot {

    private String id;

    private BigDecimal winAmount;

}
