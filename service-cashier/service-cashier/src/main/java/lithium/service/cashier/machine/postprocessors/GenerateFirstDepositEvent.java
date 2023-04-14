package lithium.service.cashier.machine.postprocessors;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.machine.DoMachineContext;
import lithium.service.cashier.services.RabbitEventService;
import lithium.service.games.client.service.GameUserStatusClientService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class GenerateFirstDepositEvent implements OnSuccessTransactionProcessor {
    private final RabbitEventService rabbitEventService;
    @Override
    public void runPostProcessor(DoMachineContext context) {
        rabbitEventService.sendFirstDepositEvent(context.getExternalUser().guid(),
            context.getTransaction(),
            context.isFirstDeposit());
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean shouldProcess(DoMachineContext context, String previousState) {
        return context.getState().equals(DoMachineState.SUCCESS)
            && context.isFirstDeposit()
            && !DoMachineState.SUCCESS.name().equalsIgnoreCase(previousState);
    }
}
