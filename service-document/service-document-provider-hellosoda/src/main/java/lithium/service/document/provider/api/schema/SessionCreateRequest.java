package lithium.service.document.provider.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateRequest {
    private String productId;
    private String jobId;
    private String mode;
}
