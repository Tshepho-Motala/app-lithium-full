package lithium.service.user.provider.threshold.data.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThesholdRevisonResultDto {

  private BigDecimal percentage;
}
