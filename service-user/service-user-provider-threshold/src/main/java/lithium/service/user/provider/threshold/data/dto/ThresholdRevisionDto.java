package lithium.service.user.provider.threshold.data.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ThresholdRevisionDto {
  private String domain;
  private BigDecimal amount;
  private BigDecimal percentage;
  private int granularity;
  private Long createdById;
  private Long modifiedById;
  private LocalDate createdDate;
  private LocalDate modifiedDate;
  private int type;
  private long id;
}
