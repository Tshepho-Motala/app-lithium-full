package lithium.service.casino.provider.roxor.api.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Replay {
    private String replayUrl;
}
