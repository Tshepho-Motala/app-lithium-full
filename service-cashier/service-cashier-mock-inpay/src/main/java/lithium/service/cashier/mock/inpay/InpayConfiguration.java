package lithium.service.cashier.mock.inpay;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix ="lithium.service.cashier.mock.inpay")
@Data
@Configuration
public class InpayConfiguration {
    private String webhookUrl;
    private String webhookUrlV2;
    private String xAuthUuid;
    private String authorization;
    private String merchantCertificate;
    private String merchantPrivateKey;
    private String inpayCertificate;
    private String inpayCaChain;
    private String apiVersion;
    private String bankOwnerName;
    private String merchantId;
    private String delayBetweenTransactionSteps;
}
