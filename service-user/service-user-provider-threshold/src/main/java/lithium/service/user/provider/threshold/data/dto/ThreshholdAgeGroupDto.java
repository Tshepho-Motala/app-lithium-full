package lithium.service.user.provider.threshold.data.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class ThreshholdAgeGroupDto {
private int ageMax;
private int ageMin;
private int type;
private BigDecimal amount;
private String domainName;
private BigDecimal thresholdPercentage;
private int granularity;


}
