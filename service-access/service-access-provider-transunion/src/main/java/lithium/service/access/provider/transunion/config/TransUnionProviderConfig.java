package lithium.service.access.provider.transunion.config;

import lombok.Data;


@Data
public class TransUnionProviderConfig {
    private String userName;
    private String password;
    private String company;
    private String baseUrl;
    private String application;
    private String timeoutConnection;
    private String timeoutRead;
    private String passwordAutoUpdate;
    private String passwordUpdateDelay;
    private String lastUpdateDate;
    private String passwordUpdateUrl;
}
