package lithium.service.limit.client.objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlayerExclusionRequest {
    private String playerGuid;
    private Long playerId;
    private String exclusionEndDate;
    private boolean permanentExclusion;
    private String advisor;
    private ExclusionSource exclusionSource;
}
