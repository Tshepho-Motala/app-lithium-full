package lithium.service.games.client.objects.supplier;


public enum DisplayTypeEnum {

    MOBILE("mobile"),
    DESKTOP("desktop"),
    ALL("all"),
    UNKNOWN("unknown");

    private String value;

    DisplayTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
