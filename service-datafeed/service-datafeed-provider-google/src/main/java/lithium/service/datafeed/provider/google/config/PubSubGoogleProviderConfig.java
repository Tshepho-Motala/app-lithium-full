package lithium.service.datafeed.provider.google.config;

import lombok.Data;
import org.joda.time.DateTime;

import java.util.Objects;


@Data
public class PubSubGoogleProviderConfig {
    private String project_id;
    private String private_key_id;
    private String private_key;
    private String client_email;
    private String client_id;
    private String userChangeTopicKey;
    private String sportsbookBetChangeTopicKey;
    private String walletTransactionsTopicKey;
    private String virtualTransactionsTopicKey;
    private String casinoTransactionsTopicKey;
    private String accountLinkFeedsTopicKey;
    private String marketingPreferencesTopicKey;
    private DateTime createDate;
    private boolean isAccountChangesActive = false;
    private boolean isWalletTransactionActive = false;
    private boolean isVirtualsFeedActive = false;
    private boolean isCasinoFeedActive = false;
    private boolean isSpotsBookFeedActive = false;
    private boolean isAccountLinkFeedActive = false;
    private boolean isMarketingPreferencesActive = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PubSubGoogleProviderConfig)) return false;
        PubSubGoogleProviderConfig config = (PubSubGoogleProviderConfig) o;
        return isAccountChangesActive() == config.isAccountChangesActive() &&
                isWalletTransactionActive() == config.isWalletTransactionActive() &&
                isVirtualsFeedActive() == config.isVirtualsFeedActive() &&
                isCasinoFeedActive() == config.isCasinoFeedActive() &&
                isSpotsBookFeedActive() == config.isSpotsBookFeedActive() &&
                isAccountLinkFeedActive() == config.isAccountLinkFeedActive() &&
                isMarketingPreferencesActive() == config.isMarketingPreferencesActive() &&
                getProject_id().equals(config.getProject_id()) &&
                getPrivate_key_id().equals(config.getPrivate_key_id()) &&
                getPrivate_key().equals(config.getPrivate_key()) &&
                getClient_email().equals(config.getClient_email()) &&
                getClient_id().equals(config.getClient_id()) &&
                getUserChangeTopicKey().equals(config.getUserChangeTopicKey()) &&
                getSportsbookBetChangeTopicKey().equals(config.getSportsbookBetChangeTopicKey()) &&
                getWalletTransactionsTopicKey().equals(config.getWalletTransactionsTopicKey()) &&
                getVirtualTransactionsTopicKey().equals(config.getVirtualTransactionsTopicKey()) &&
                getCasinoTransactionsTopicKey().equals(config.getCasinoTransactionsTopicKey()) &&
                getAccountLinkFeedsTopicKey().equals(config.getAccountLinkFeedsTopicKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProject_id(), getPrivate_key_id(), getPrivate_key(), getClient_email(), getClient_id(), getUserChangeTopicKey(), getSportsbookBetChangeTopicKey(), getWalletTransactionsTopicKey(), getVirtualTransactionsTopicKey(), getCasinoTransactionsTopicKey(), getAccountLinkFeedsTopicKey(),
                getMarketingPreferencesTopicKey(), isAccountChangesActive(), isWalletTransactionActive(), isVirtualsFeedActive(), isCasinoFeedActive(), isSpotsBookFeedActive(), isAccountLinkFeedActive(),
                isMarketingPreferencesActive());
    }
}
