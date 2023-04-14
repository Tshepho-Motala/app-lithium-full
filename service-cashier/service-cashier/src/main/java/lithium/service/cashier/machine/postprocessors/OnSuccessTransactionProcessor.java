package lithium.service.cashier.machine.postprocessors;

import lithium.service.cashier.client.frontend.DoMachineState;

import lithium.service.cashier.machine.DoMachineContext;

import java.util.List;


public interface OnSuccessTransactionProcessor {
    void runPostProcessor(DoMachineContext context);
    int getPriority();
    boolean shouldProcess(DoMachineContext context, String previousState);
}
