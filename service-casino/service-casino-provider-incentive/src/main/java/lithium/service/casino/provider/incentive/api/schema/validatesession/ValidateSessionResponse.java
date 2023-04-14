
package lithium.service.casino.provider.incentive.api.schema.validatesession;

import com.fasterxml.jackson.annotation.JsonRawValue;
import lithium.math.CurrencyAmount;
import lombok.Data;

import java.math.BigDecimal;

@Data
@SuppressWarnings("unused")
public class ValidateSessionResponse {

    private CurrencyAmount balance;
    private String cellphoneNumber;
    private String currencyCode;
    private String guid;
    private String username;

}
