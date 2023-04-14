package lithium.service.reward.enums;

import lithium.service.notifications.client.dtos.INotificationType;

public enum NotificationTypes implements INotificationType {
    REWARD("reward");

    private String type;

    NotificationTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
