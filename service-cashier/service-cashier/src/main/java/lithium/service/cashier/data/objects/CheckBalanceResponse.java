package lithium.service.cashier.data.objects;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckBalanceResponse {
    private boolean enoughBalance;
    private String message;
}
