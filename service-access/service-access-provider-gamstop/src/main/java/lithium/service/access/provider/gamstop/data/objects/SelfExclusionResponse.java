package lithium.service.access.provider.gamstop.data.objects;

import lithium.service.access.provider.gamstop.data.enums.ExclusionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SelfExclusionResponse {
    private ExclusionType exclusionType;
    private String xUniqueId;
}
