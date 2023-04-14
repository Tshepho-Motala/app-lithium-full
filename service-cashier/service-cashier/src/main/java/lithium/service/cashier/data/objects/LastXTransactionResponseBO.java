package lithium.service.cashier.data.objects;

import lithium.service.cashier.data.entities.backoffice.ShortenCashierTransactionBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastXTransactionResponseBO {
    private Long userId;
    private String domainName;
    private List<ShortenCashierTransactionBO> lastXTransactions;
}
