package lithium.service.cashier.mock.paynl.data.errors;

import lithium.service.cashier.processor.paynl.exceptions.Error;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private Error method;
    private IBan iBan;
}
