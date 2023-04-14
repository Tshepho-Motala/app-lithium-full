package lithium.service.cashier.mock.smartcash.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix="lithium.service.cashier.mock.smartcash")
@Data
public class SmartcashConfigurationProperties {
    private String hashKey = "a2d4766e94924fa0b5f92bcbadfdff76";
    private Long notificationDelay = 5000L;
    private String notificationUrl = "/service-cashier-processor-smartcash/public/webhook";
    private AuthenticationConfiguration authentication = new AuthenticationConfiguration();
}
