package lithium.service.cashier.processor.paypal.data;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = {"username", "password"})
public class TokenContext {
    private String username;
    private String password;
    private String apiUrl;
    private String id;
    private String responseLog;
}
