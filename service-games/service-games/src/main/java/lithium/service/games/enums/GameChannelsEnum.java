package lithium.service.games.enums;

public enum GameChannelsEnum {

    DESKTOP_WEB ("desktop_web"),
    MOBILE_WEB ("mobile_web"),
    MOBILE_IOS("mobile_ios"),
    ANDROID_NATIVE("android_native");

    public final String gameChannelName;

    GameChannelsEnum(String gameChannelName) {
        this.gameChannelName = gameChannelName;
    }

    @Override
    public String toString() {
        return gameChannelName;
    }

    public static GameChannelsEnum byName(String name) {
        for (GameChannelsEnum value : values()) {
            if(value.gameChannelName.equals(name)) {
                return value;
            }
        }
        return null;
    }

}
