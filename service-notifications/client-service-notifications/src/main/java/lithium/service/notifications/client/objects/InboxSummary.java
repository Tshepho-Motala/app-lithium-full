package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InboxSummary {
    private int ctaCount;
    private int readCount;
    private int unreadCount;
}
