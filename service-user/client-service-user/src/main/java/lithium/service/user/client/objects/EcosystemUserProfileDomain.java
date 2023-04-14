package lithium.service.user.client.objects;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class EcosystemUserProfileDomain {
    private long id;
    private int version;
    private String name;
    private String defaultLocale;
    private String defaultCountry;
    private String defaultCurrency;
}
