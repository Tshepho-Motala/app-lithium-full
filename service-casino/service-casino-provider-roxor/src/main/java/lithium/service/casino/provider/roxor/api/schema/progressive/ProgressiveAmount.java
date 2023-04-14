package lithium.service.casino.provider.roxor.api.schema.progressive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProgressiveAmount {
    private BigDecimal amount;
    private String currency;
}
