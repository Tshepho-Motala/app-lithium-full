package lithium.service.limit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLogLimit {
    private String playerGuid;
    private String domainName;
    private String granularity;
    private String amount;
    private String type;
}
