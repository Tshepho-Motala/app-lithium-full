package lithium.service.access.provider.google.recaptcha.config;

import lombok.Data;

@Data
public class RecaptchaProviderConfig {
    private String secretKey;
    private String siteKey;
    private String recaptchaServiceUrl;
    private double score;
    private Integer connectTimeout = 60000;
    private Integer connectionRequestTimeout = 60000;
    private Integer socketTimeout = 60000;
}
