package lithium.service.cashier.processor.interswitch.api.schema;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AccountReceivable {
    private String accountNumber;
    private String accountType;
}
