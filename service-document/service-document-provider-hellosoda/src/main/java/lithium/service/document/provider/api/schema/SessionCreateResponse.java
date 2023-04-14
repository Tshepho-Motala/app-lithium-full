package lithium.service.document.provider.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCreateResponse {
    private Boolean success;
    private Integer status;
    private SessionInfo data;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionInfo {
        private Session session;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Session {
            private String status;
            private String mode;
            private Boolean purged;
            private String _id;
            private String productId;
            private String jobId;
            private List<String> requests;
            private String created_at;
            private String updated_at;
            private Integer __v;
        }
    }
}
