package lithium.service.client.objects.placeholders;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@AllArgsConstructor
public enum SourceService {
    SERVICE_USER("service-user"),
    SERVICE_CASHIER("service-cashier"),
    SERVICE_LIMIT("service-limit"),
    SERVICE_DOMAIN("service-domain");
    @Getter
    @Accessors(fluent = true)
    private String serviceUrl;
}
