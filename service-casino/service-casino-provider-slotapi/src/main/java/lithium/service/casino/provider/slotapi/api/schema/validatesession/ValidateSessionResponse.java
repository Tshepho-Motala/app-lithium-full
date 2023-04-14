
package lithium.service.casino.provider.slotapi.api.schema.validatesession;

import lombok.Data;

@Data
@SuppressWarnings("unused")
public class ValidateSessionResponse {

    private double balance;
    private String cellphoneNumber;
    private String currencyCode;
    private String guid;
    private String username;

}
