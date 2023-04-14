package lithium.service.user.provider.threshold.data.dto;

import lombok.Data;

@Data
public class AgeRangeDto {

  private String domainName;
  private int previousAgeMin;
  private int previousAgeMax;
  private int nextAgeMin;
  private int nextAgeMax;
  private int[] idsToEdit;
}
