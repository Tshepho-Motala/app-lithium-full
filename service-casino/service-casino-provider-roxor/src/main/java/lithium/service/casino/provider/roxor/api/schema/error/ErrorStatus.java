package lithium.service.casino.provider.roxor.api.schema.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorStatus {
    private String code; //ErrorCode
    private ErrorObject error;
}
