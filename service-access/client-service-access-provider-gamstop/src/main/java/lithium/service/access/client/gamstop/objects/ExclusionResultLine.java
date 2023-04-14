package lithium.service.access.client.gamstop.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExclusionResultLine {
    private String userGuid;
    private String username;
    private Long reportRunResultsId;
    private ExclusionResult exclusionResult;
}
