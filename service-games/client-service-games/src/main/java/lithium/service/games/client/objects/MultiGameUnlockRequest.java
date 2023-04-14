package lithium.service.games.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class MultiGameUnlockRequest {
    private String userGuid;
    private List<String> gameGuids;
}
