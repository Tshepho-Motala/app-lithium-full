package lithium.service.cashier.services.autowithdrawal;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.services.TransactionService;
import lithium.service.user.client.objects.User;
import lombok.Builder;
import lombok.Getter;

import static java.util.Objects.isNull;

@Builder
public class RuleValidateContext {
    @Getter
    private User user;
    @Getter
    private Transaction transaction;
    @Getter
    private DomainMethod domainMethod;

    public String guid() {
        return user.guid();
    }
}
