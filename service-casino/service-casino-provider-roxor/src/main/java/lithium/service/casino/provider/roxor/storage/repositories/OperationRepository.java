package lithium.service.casino.provider.roxor.storage.repositories;

import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.entities.OperationType;
import lithium.service.casino.provider.roxor.storage.entities.Type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, Long> {
    Operation findByTransferId(String transferId);

    List<Operation> findByGamePlayAndTransferId(GamePlay gamePlay, String transferId);

    Operation findByGamePlayAndOperationTypeAndTransferId(GamePlay gamePlay, OperationType operationType, String transferId);

    Operation findByAccrualId(String accrualId);

    List<Operation> findByGamePlayAndType(GamePlay gamePlay, Type type);

    List<Operation> findByGamePlayOrderByIdAsc(GamePlay gamePlay);

    List<Operation> findByGamePlayGuid(String gamePlayId);
}
