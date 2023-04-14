package lithium.service.casino.provider.roxor.api.schema.auth;

import lithium.service.casino.provider.roxor.api.schema.Player;
import lithium.service.casino.provider.roxor.api.schema.SuccessStatus;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class AuthenticatePlayerResponse {
    private SuccessStatus status;
    private Player player;
}
