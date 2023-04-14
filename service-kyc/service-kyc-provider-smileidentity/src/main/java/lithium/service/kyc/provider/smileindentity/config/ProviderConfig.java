package lithium.service.kyc.provider.smileindentity.config;

import lombok.Data;

@Data
public class ProviderConfig {
    private String verifyApiUrl;
    private String partnerId;
    private String apiKey;
    private String bankListUrl;
    private String country;
    private Boolean passportNumber;
    private Boolean nationalId;
    private Boolean nin;
    private Boolean driversLicence;
    private Boolean bankAccount;
    private Boolean bvn;
    private Boolean voterId;
	private Boolean ninPhoneNumber;
    private Integer connectTimeout = 60000;
    private Integer connectionRequestTimeout = 60000;
    private Integer socketTimeout = 60000;
}
