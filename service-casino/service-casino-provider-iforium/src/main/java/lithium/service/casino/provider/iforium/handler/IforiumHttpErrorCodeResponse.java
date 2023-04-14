package lithium.service.casino.provider.iforium.handler;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IforiumHttpErrorCodeResponse {
    private Integer status;
    private String lithiumStatusCode;
    private String message;
    private String errorCode;
}
