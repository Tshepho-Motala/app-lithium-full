package lithium.service.datafeed.provider.google.objects;

import lombok.Getter;

public enum DataType {
    ACCOUNT_CHANGES("account-changes"),
    WALLET_TRANSACTIONS("wallet-transaction"),
    SPORTSBOOK_TRANSACTIONS("sportsbook-transactions"),
    VIRTUAL_TRANSACTIONS("virtual-transactions"),
    CASINO_TRANSACTIONS("casino-transactions"),
    ACCOUNT_LINK_CHANGES("account-link-changes"),
    MARKETING_PREFERENCES("marketing-preferences");

    DataType(String channelName) {
        this.channelName=channelName;
    }

    @Getter
    private final String channelName;
}
