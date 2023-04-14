package lithium.service.geo.client.objects;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Country {

    String code;
    String iso3;
    Integer isonr;
    String fips;
    String name;
    String capital;
    Long sqkm;
    Long population;
    String continent;
    String topLevelDomain;
    String currencyCode;
    String currencyName;
    String phone;
    String postalCodeFormat;
    String postalCodeRegex;
    String languages;
    String neighbours;
    String equivalentFips;
    Boolean enabled;

}
