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
public class IBan {
    private Error number;
    private Error bic;
    private Error holder;
}
