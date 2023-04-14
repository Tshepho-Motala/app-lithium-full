package lithium.service.cashier.processor.smartcash.data;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class TransactionRequestData {
    @NotEmpty
    private String amount;
    @NotEmpty
    private String id;
}
