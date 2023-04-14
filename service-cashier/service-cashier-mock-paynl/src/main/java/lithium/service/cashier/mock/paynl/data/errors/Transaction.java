package lithium.service.cashier.mock.paynl.data.errors;

import com.fasterxml.jackson.annotation.JsonInclude;
import lithium.service.cashier.processor.paynl.exceptions.Error;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    private Error type;
    private Error serviceId;
    private Amount amount;
}
