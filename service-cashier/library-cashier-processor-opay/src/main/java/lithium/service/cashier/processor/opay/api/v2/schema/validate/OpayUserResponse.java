package lithium.service.cashier.processor.opay.api.v2.schema.validate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpayUserResponse {
    private String code;
    private String message;
    private User data;

    @Data
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class User {
        private String userId;
        private String phoneNumber;
        private String firstName;
        private String lastName;
        private String email;
        private String address;
    }
}
