package lithium.service.limit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLogAgeLimit {
    private String createdByGuid;
    private String modifiedByGuid;
    private String domainName;
    private String granularity;
    private String amount;
    private String type;
    private String ageMax;
    private String ageMin;
}
