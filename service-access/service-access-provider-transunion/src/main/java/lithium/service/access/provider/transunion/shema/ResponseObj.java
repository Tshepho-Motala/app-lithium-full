package lithium.service.access.provider.transunion.shema;

import lithium.service.access.client.objects.RawAuthorizationData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseObj {
    private boolean success;
    private String type;
    private RawAuthorizationData rawAuthorizationData;
}
