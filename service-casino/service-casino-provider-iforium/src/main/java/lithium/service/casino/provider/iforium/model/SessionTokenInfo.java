package lithium.service.casino.provider.iforium.model;

import lombok.Data;

@Data
public class SessionTokenInfo {

    private final String sessionKey;

    private final long issuedTimeMillis;
}
