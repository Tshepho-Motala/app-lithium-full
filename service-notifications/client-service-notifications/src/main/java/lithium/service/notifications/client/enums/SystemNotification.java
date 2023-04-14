package lithium.service.notifications.client.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public enum SystemNotification {

    INTERVENTION_MESSAGE_1("intervention_message.1",
            "30 Days Intervention Message",
            "When the INTERVENTION_COMPS_BLOCK system restriction is created with a subordinate type = 1, then the 30 days intervention message notification needs to be sent to the player.",
            "%translationKey%",
            "PULL",
            true,
            "",
            "en"),
    INTERVENTION_MESSAGE_2("intervention_message.2",
            "60 Days Intervention Message",
            "When the INTERVENTION_COMPS_BLOCK system restriction is created with a subordinate type = 2, then the 60 days intervention message notification needs to be sent to the player.",
            "%translationKey%",
            "PULL",
            true,
            "",
            "en"),
    INTERVENTION_MESSAGE_3("intervention_message.3",
            "90 Days Intervention Message",
            "When the INTERVENTION_COMPS_BLOCK system restriction is created with a subordinate type = 3, then the 90 days intervention message notification needs to be sent to the player.",
            "%translationKey%",
            "PULL",
            true,
            "",
            "en");
    @Getter
    @Accessors(fluent=true)
    private String notificationName;

    @Getter
    @Accessors(fluent=true)
    private String displayName;

    @Getter
    @Accessors(fluent=true)
    private String description;

    @Getter
    @Accessors(fluent=true)
    private String message;

    @Getter
    @Accessors(fluent=true)
    private String channel;

    @Getter
    @Accessors(fluent=true)
    private Boolean forced;

    @Getter
    @Accessors(fluent = true)
    private String templateName;

    @Getter
    @Accessors(fluent = true)
    private String templateLang;

    @JsonCreator
    public static SystemNotification fromNotificationName(String name) {
        for (SystemNotification s: SystemNotification.values()) {
            if (s.notificationName.equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}
