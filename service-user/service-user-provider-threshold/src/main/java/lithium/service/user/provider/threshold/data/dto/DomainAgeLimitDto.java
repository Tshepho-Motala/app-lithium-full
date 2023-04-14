package lithium.service.user.provider.threshold.data.dto;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class DomainAgeLimitDto {
    private long amount;
    private String domainName;
    private int granularity;
    private int ageMax;
    private int ageMin;
    private int type;
    private String creatorGuid;
    private BigDecimal warningThreshold;
}
