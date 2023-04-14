package lithium.service.domain.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DomainAttributesData {
    private String domainName;
    private String defaultTimezone;
}
