package lithium.service.limit.client.schemas.exclusion;

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
public class RemoveExclusionRequest {

    private String playerGuid;
    private String authorGuid;
}
