package lithium.service.limit.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeLogTimeSlotLimit {
    private String playerGuid;
    private String domainName;
    private String limitFromUtc;
    private String limitToUtc;
}
