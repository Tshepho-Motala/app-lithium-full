package lithium.service.limit.data.dto;

import lombok.Data;

@Data
public class SaveDomainAgeLimitDto {
    private long amount;
    private String domainName;
    private int granularity;
    private int ageMax;
    private int ageMin;
    private int type;
    private String creatorGuid;
}
