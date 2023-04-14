package lithium.service.casino.provider.roxor.services;

import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TransferOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TypeEnum;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.entities.OperationType;
import lithium.service.casino.provider.roxor.storage.entities.Type;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationTypeRepository;
import lithium.service.casino.provider.roxor.storage.repositories.TypeRepository;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class GamePlayPhase1PositiveMocks {

    public static GamePlayRepository mockGamePlayRepository() {
        GamePlay gamePlay = new GamePlay();
        gamePlay.setRoxorStatus(GamePlay.RoxorStatus.STARTED);
        GamePlayRepository gamePlayRepository = Mockito.mock(GamePlayRepository.class);
        Mockito.when(gamePlayRepository.findByGuid(Mockito.anyString()))
                .thenReturn(gamePlay);
        return gamePlayRepository;
    }

    public static OperationTypeRepository mockOperationTypeRepository() {
        OperationTypeRepository operationTypeRepository = Mockito.mock(OperationTypeRepository.class);
        Mockito.when(operationTypeRepository.findByCode(OperationTypeEnum.TRANSFER.name()))
                .thenReturn(new OperationType());
        return operationTypeRepository;
    }

    public static OperationRepository mockOperationRepository() {
        OperationRepository operationRepository = Mockito.mock(OperationRepository.class);
        Mockito.when(operationRepository.findByGamePlayAndOperationTypeAndTransferId(
                Mockito.any(GamePlay.class),
               Mockito.any(OperationType.class),
                Mockito.anyString()
        )).thenReturn(null);

        List<Operation>operations = new ArrayList<Operation>();
        operations.add(new Operation());
        Mockito.when(operationRepository.findByGamePlayAndType(
                Mockito.any(GamePlay.class),
                Mockito.any(Type.class)
        )).thenReturn(operations);

        Operation operation = Operation.builder().amountCents((long)100000).build();
        Mockito.when( operationRepository.findByGamePlayAndOperationTypeAndTransferId(
                Mockito.any(GamePlay.class),
                Mockito.any(OperationType.class),
                Mockito.anyString()
        )).thenReturn(operation);

        return operationRepository;
    }

    public static OperationRepository mockOperationRepositoryPt2() {
        OperationRepository operationRepository = Mockito.mock(OperationRepository.class);
        Mockito.when(operationRepository.findByGamePlayAndOperationTypeAndTransferId(
                Mockito.any(GamePlay.class),
                Mockito.any(OperationType.class),
                Mockito.anyString()
        )).thenReturn(null);

        List<Operation>operations = new ArrayList<Operation>();
        operations.add(new Operation());
        Mockito.when(operationRepository.findByGamePlayAndType(
                Mockito.any(GamePlay.class),
                Mockito.any(Type.class)
        )).thenReturn(null);

        Operation operation = Operation.builder().amountCents((long)100000).build();
        Mockito.when( operationRepository.findByGamePlayAndOperationTypeAndTransferId(
                Mockito.any(GamePlay.class),
                Mockito.any(OperationType.class),
                Mockito.anyString()
        )).thenReturn(null);
        return operationRepository;
    }


    public static TypeRepository mockTypeRepository() {
        TypeRepository typeRepository = Mockito.mock(TypeRepository.class);
        Mockito.when(typeRepository.findByCode(Mockito.anyString())).thenReturn(new Type());
        return typeRepository;
    }

}
