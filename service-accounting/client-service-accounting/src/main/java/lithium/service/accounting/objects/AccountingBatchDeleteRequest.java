package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountingBatchDeleteRequest {
    private List<Long> transactionIds;
}
