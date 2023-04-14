package lithium.service.cashier.mock.flutterwave;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "lithium.service.cashier.mock.flutterwave")
@Data
@Configuration
public class FlutterwaveConfiguration {
    private String webhookSchedulingInMilliseconds;
    private String webhookUrl;
}
