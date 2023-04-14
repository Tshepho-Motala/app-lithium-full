
package lithium.service.casino.provider.sportsbook.api.schema.validatesession;

import lithium.math.CurrencyAmount;
import lombok.Data;

@Data
public class CustomerInfoResponse {

    private String guid;
    private String username;
    private String city;
    private String country;
    private String currencyCode;
    private CurrencyAmount balance;
    private long sessionId;

}
