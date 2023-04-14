package lithium.service.casino.provider.sportsbook.config;

import lombok.Data;

@Data
public class ProviderConfig {
    private String hashPassword;
    private String externalTransactionInfoUrl;
    private String playerOffset;
    private String betSearchUrl;
    private String betSearchKey;
    private String betSearchBrand;
    private String sportsFreeBetsUrl;
    private String bonusRestrictionUrl;
    private String bonusRestrictionKey;
}
