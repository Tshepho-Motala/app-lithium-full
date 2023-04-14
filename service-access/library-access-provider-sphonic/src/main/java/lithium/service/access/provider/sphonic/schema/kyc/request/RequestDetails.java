package lithium.service.access.provider.sphonic.schema.kyc.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestDetails {
    String requestId;
    String requestDateTime;
}
