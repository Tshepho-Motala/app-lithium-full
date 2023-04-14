package lithium.service.datafeed.provider.google.service;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.MessageChannel;

public interface PubSubExchangeQueue {
    @Output("service.google.pub-sub.user")
    MessageChannel channelUserChange();

    @Output("service.google.pub-sub.sportsbook")
    MessageChannel channelSportsbookChange();

    @Output("service.google.pub-sub.wallet")
    MessageChannel channelWalletChange();

    @Output("service.google.pub-sub.virtual")
    MessageChannel channelVirtualChange();

    @Output("service.google.pub-sub.casino")
    MessageChannel channelCasinoChange();

    @Output("service.google.pub-sub.link")
    MessageChannel channelAccountLincChange();

    @Output("service.google.pub-sub.marketing-preferences")
    MessageChannel channelMarketingPreferences();
}
