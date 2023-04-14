package lithium.service.reward.provider.casino.roxor;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BonusCash {

  private String currency;
  private BigDecimal initial;
  private BigDecimal redeemed;
  private BigDecimal balance;
}
