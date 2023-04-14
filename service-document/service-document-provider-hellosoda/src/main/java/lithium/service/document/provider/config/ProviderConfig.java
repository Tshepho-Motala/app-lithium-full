package lithium.service.document.provider.config;

import lombok.Data;

@Data
public class ProviderConfig {
    private String profileApiV1Url;
    private String profileApiUrl;
    private String iDocufyApiUrl;
    private String profileBearer;
    private String productId;
}
