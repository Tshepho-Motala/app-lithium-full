package lithium.service.cashier.processor.paynl.data.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IBan {
    private String number;
    private String bic;
    private String holder;
}
