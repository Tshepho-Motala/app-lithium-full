package lithium.service.cashier.machine;

import lithium.service.cashier.machine.postprocessors.OnSuccessTransactionProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PostProcessorService {
    private final  List<OnSuccessTransactionProcessor> changeTransactionStateProcessors;

    public void proceedOnTransactionStateChange(DoMachineContext context, String previousState) {
        changeTransactionStateProcessors.stream()
                .sorted(Comparator.comparing(OnSuccessTransactionProcessor::getPriority))
                .filter(processor -> processor.shouldProcess(context, previousState))
                .forEach(filteredProcessor -> filteredProcessor.runPostProcessor(context));
    }

}
