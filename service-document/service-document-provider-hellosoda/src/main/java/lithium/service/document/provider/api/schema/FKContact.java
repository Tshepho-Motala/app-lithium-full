package lithium.service.document.provider.api.schema;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FKContact {
    private String email;
}
