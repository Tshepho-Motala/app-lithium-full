package lithium.service.games.client.objects;

import lithium.service.games.client.enums.UserGameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class SimpleGameUserStatus {
    private String userGuid;
    private String gameName;
    private String gameGuid;
    private UserGameStatus status;
}
