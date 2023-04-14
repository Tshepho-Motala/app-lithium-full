package lithium.service.cashier.mock.smartcash.configuration;

import lombok.Data;
@Data
public class AuthenticationConfiguration {
    private String clientId = "407531ff-10a9-435e-a7b9-dda9f4e13e07";
    private String clientSecret = "20643a01-37a3-47af-a4d5-880b687a212e";
    private Long expiresIn = 720000L;
}
