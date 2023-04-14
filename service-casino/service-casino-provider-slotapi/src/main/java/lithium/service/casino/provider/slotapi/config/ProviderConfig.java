package lithium.service.casino.provider.slotapi.config;

import lombok.Data;

@Data
public class ProviderConfig {
    private String hashPassword;
    private String betHistoryRoundDetailUrl;
    private String betHistoryRoundDetailPid;
}
