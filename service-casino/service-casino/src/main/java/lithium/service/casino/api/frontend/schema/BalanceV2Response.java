package lithium.service.casino.api.frontend.schema;

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
public class BalanceV2Response {
    private BigDecimal balance;
    private Integer verificationLevel;
}
