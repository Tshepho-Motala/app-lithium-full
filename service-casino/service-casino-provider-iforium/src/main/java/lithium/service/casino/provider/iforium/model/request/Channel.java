package lithium.service.casino.provider.iforium.model.request;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Channel {

    DESKTOP("desktop"),
    MOBILE("mobile");

    private final String channelId;

    Channel(String channelId) {
        this.channelId = channelId;
    }

    public static Channel valueOf(String value, Channel defaultValue) {
        return Arrays.stream(values()).filter(c -> c.getChannelId().equalsIgnoreCase(value)).findFirst().orElse(defaultValue);
    }
}
