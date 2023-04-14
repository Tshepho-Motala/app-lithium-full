package lithium.service.casino.provider.iforium.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CharacterPatterns {

    public static final String CURRENCY_PATTERN = "^[a-zA-Z]{3}$";

    public static final String OPERATOR_ACCOUNT_ID_PATTERN = "(?<domainName>.*)/(?<playerId>.*)";

    public static final String SESSION_TOKEN_PATTERN = "^(?<sessionKey>.+)-(?<timestampHex>.+)$";
}
