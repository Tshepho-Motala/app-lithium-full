package lithium.service.cashier.machine.postprocessors;

import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class UpdateProtectionOfCustomerFundsVersion  implements OnSuccessTransactionProcessor {

    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;
    @Override

    public void runPostProcessor(DoMachineContext context) {
        updateProtectionOfCustomerFundsVersionIfEnabled(context);
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public boolean shouldProcess(DoMachineContext context, String previousState) {
        return context.getType() == TransactionType.DEPOSIT
                && !context.getTransaction().isManual()
                && "START".equals(previousState);
    }

    private void updateProtectionOfCustomerFundsVersionIfEnabled(DoMachineContext context) {
        User user = context.getUser();
        try {
            if (cachingDomainClientService.isProtectionOfCustomerFundsEnabled(user.domainName())) {
                lithium.service.user.client.objects.User externalUser =
                        userApiInternalClientService.updateProtectionOfCustomerFundsVersion(user.guid());
                context.setExternalUser(externalUser);
            }
        } catch (Exception e) {
            log.error("Failed to update player protection of funds version [user.guid="+user.guid()+"] " + e.getMessage(), e);
        }
    }
}
