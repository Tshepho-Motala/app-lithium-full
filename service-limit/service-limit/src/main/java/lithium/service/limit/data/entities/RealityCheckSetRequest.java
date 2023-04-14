package lithium.service.limit.data.entities;

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
public class RealityCheckSetRequest {
    private String playerGuid;
    private long newRealityCheckTime;
}
