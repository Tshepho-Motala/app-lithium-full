package lithium.service.accounting.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetDeposit {
    private Long summaryDepositCents;
    private Long summaryDepositCount;
    private Long summaryWithdrawalCents;
    private Long summaryWithdrawalCount;
    private String currency;
}
