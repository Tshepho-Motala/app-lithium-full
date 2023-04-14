package lithium.service.user.client.objects;

public enum PubSubEventOrigin {
    BACK_OFFICE("BACKOFFICE"), USER("USER"), SYSTEM("SYSTEM");
    private final String name;

    PubSubEventOrigin(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
