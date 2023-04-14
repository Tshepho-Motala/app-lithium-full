package lithium.service.casino.provider.roxor.api.schema.auth;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthenticatePlayerRequest {
    private String playerId;
    private String website;
}
