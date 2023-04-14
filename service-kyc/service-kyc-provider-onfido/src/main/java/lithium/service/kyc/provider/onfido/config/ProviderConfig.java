package lithium.service.kyc.provider.onfido.config;

import lombok.Data;

import java.util.List;

@Data
public class ProviderConfig {
    private String baseUrl;
    private String apiToken;
    private List<String> reportNames;
    private String[] webhookIds;
    private boolean matchDocumentAddress;
    private boolean matchFirstName;
    private List<String> supportedIssuingCountries;
}


