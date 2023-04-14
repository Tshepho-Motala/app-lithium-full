package lithium.service.casino.provider.roxor.api.schema.error;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorObject {
    private String displayMessage;
    private Integer category; //ErrorCategory
}
