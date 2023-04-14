package lithium.service.document.provider.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Boolean success;
    private Integer status;
    private AuthInfo data;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthInfo {
        private String token;
    }
}
