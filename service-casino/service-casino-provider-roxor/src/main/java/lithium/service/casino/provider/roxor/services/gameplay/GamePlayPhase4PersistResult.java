package lithium.service.casino.provider.roxor.services.gameplay;

import lithium.metrics.SW;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.GamePlayRequest;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRequestRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class GamePlayPhase4PersistResult {
    @Autowired GamePlayRequestRepository gamePlayRequestRepository;
    @Autowired GamePlayRepository gamePlayRepository;
    @Autowired OperationRepository operationRepository;
    @Autowired ValidationHelper validationHelper;


    public void persistResult(GamePlayContext context) {
        SW.start("gameplay.presist.result");
        try {
            List<Operation> processingOperationList = context.getOperationEntityList().stream()
                    .filter(o -> o.getStatus().equals(Operation.Status.PROCESSING))
                    .collect(toList());

            int index = 0;
            for (Operation operation : processingOperationList) {
                operation.setLithiumAccountingId(
                        Long.valueOf(context.getOperationOutcomeList().get(index++).getExtSystemTransactionId()));
                operation.setStatus(Operation.Status.RESULT);

                operationRepository.save(operation);
            }

            context.getGamePlayRequestEntity().setBalanceAfter(context.getBalanceAfter());
            context.getGamePlayRequestEntity().setStatus(GamePlayRequest.Status.SUCCESS);
            context.setGamePlayRequestEntity(gamePlayRequestRepository.save(context.getGamePlayRequestEntity()));

            if (context.getRoxorFinishPresent()) {
                context.getGamePlayEntity().setBalanceAfter(context.getBalanceAfter());
                context.getGamePlayEntity().setRoxorStatus(GamePlay.RoxorStatus.FINISHED);
                context.setGamePlayEntity(gamePlayRepository.save(context.getGamePlayEntity()));
            }
        } finally {
            SW.stop();
        }
    }
}
