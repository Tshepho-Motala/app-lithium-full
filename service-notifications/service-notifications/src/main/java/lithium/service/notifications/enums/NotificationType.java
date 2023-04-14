package lithium.service.notifications.enums;

import lithium.service.notifications.client.dtos.INotificationType;

public enum NotificationType implements INotificationType {
    DEFAULT("default");

    private String type;

    NotificationType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
