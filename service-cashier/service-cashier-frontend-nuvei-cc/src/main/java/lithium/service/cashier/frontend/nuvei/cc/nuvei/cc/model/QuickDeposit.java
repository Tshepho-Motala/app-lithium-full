package lithium.service.cashier.frontend.nuvei.cc.nuvei.cc.model;

import lithium.service.cashier.client.objects.ProcessorAccount;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QuickDeposit {
    private List<ProcessorAccount> processorAccounts;
}
