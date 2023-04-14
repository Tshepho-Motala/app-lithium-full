package lithium.service.cashier.machine.postprocessors;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.cashier.services.PubSubWalletTransactionService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class NotifyPubSubAboutCompleteCashierTransaction implements OnSuccessTransactionProcessor {

    private final PubSubWalletTransactionService pubSubWalletTransactionService;

    @Override
    public void runPostProcessor(DoMachineContext context) {
        try {
            pubSubWalletTransactionService.buildAndSendWalletTransactionMessage(context.getTransaction(), context.isFirstDeposit());
        } catch (Exception e) {
            try {
                log.error("Unable to send pubsub: " + context.toString(), e);
            } catch (Exception e2) {
                log.error("Unable to send pubsub and unable to log context", e2);
            }
        }
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean shouldProcess(DoMachineContext context, String previousState) {
        //run when ever called like it is now. Should it be called on state change?
        //return context.getState().name().equalsIgnoreCase(previousState);
        return true;
    }
}
