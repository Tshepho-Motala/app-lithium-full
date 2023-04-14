package lithium.service.user.provider.sphonic.idin.objects;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class RequestDetails {
    private String requestId;
    private String requestDateTime;
}
