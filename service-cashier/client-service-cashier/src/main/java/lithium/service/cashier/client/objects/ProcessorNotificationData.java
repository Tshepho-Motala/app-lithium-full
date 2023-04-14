package lithium.service.cashier.client.objects;

import lithium.service.client.objects.placeholders.Placeholder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorNotificationData {
    private String templateName;
    private String[] recipientTypes;
    private Set<Placeholder> placeholders;
    private String to;
}
