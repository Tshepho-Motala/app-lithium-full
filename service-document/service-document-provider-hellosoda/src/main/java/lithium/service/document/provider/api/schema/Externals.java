package lithium.service.document.provider.api.schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Externals {
    private IdCheck idcheck;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IdCheck {
        private String id;
    }
}
