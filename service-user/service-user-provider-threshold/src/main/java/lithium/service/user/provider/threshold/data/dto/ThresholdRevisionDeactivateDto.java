package lithium.service.user.provider.threshold.data.dto;

import lombok.Data;

@Data
public class ThresholdRevisionDeactivateDto {

  private String domain;
  private int granularity;
}
