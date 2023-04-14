package lithium.service.casino.provider.roxor.services.gameplay;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lithium.jpa.exceptions.CannotAcquireLockException;
import lithium.metrics.SW;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.schema.gameplay.AccrualCancelOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.AccrualOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.LifecycleFinishOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.api.schema.gameplay.RewardCancelOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.RewardOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TransferCancelOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TransferOperation;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.Currency;
import lithium.service.casino.provider.roxor.storage.entities.Game;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.GamePlayRequest;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.entities.OperationType;
import lithium.service.casino.provider.roxor.storage.entities.Platform;
import lithium.service.casino.provider.roxor.storage.entities.RewardBonusMap;
import lithium.service.casino.provider.roxor.storage.entities.Source;
import lithium.service.casino.provider.roxor.storage.entities.Type;
import lithium.service.casino.provider.roxor.storage.entities.User;
import lithium.service.casino.provider.roxor.storage.repositories.CurrencyRepository;
import lithium.service.casino.provider.roxor.storage.repositories.DomainRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRequestRepository;
import lithium.service.casino.provider.roxor.storage.repositories.GameRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationTypeRepository;
import lithium.service.casino.provider.roxor.storage.repositories.PlatformRepository;
import lithium.service.casino.provider.roxor.storage.repositories.RewardBonusMapRepository;
import lithium.service.casino.provider.roxor.storage.repositories.SourceRepository;
import lithium.service.casino.provider.roxor.storage.repositories.TypeRepository;
import lithium.service.casino.provider.roxor.storage.repositories.UserRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.reward.client.QueryRewardClientService;
import lithium.service.reward.client.dto.PlayerRewardHistoryStatus;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.util.ExceptionMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GamePlayPhase1Persist {
    final @Setter GamePlayRequestRepository gamePlayRequestRepository;
    final @Setter UserRepository userRepository;
    final @Setter CurrencyRepository currencyRepository;
    final @Setter OperationRepository operationRepository;
    final @Setter DomainRepository domainRepository;
    final @Setter GameRepository gameRepository;
    final @Setter GamePlayRepository gamePlayRepository;
    final @Setter OperationTypeRepository operationTypeRepository;
    final @Setter PlatformRepository platformRepository;
    final @Setter TypeRepository typeRepository;
    final @Setter SourceRepository sourceRepository;
    final @Setter ValidationHelper validationHelper;
    final @Setter QueryRewardClientService queryRewardClientService;
    final @Setter RewardBonusMapRepository rewardBonusMapRepository;

    @Transactional()
    public void persist(
            GamePlayContext gamePlayContext,
            String sessionKey,
            String gamePlayId
    ) throws
            Status400BadRequestException
    {
        log.debug("gameplay.persist " + gamePlayContext.getRequestJsonString());
        SW.start("gameplay.persist.create_gameplayrequest");
        GamePlayRequest gamePlayRequestEntity = GamePlayRequest.builder()
                .headerSessionKey(sessionKey)
                .headerGamePlayId(gamePlayId)
                .status(GamePlayRequest.Status.CAPTURED)
                .build();

        gamePlayRequestEntity = gamePlayRequestRepository.save(gamePlayRequestEntity);

        final lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayRequest gamePlayRequestObject;

        if (gamePlayContext.getRequest() != null) {
            gamePlayRequestObject = gamePlayContext.getRequest();
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                gamePlayRequestObject = objectMapper.readValue(
                        gamePlayContext.getRequestJsonString(),
                        lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayRequest.class);
            } catch (
                    IOException e
            ) {
                gamePlayRequestEntity.setStatus(GamePlayRequest.Status.ERROR);
                gamePlayRequestEntity.setStatusReason("Unable to parse JSON request");
                gamePlayRequestEntity = gamePlayRequestRepository.save(gamePlayRequestEntity);
                log.error("gamePlayPersist : " + ExceptionMessageUtil.allMessages(e), e);
                throw new Status400BadRequestException();
            } finally {
                SW.stop();
            }
        }

        gamePlayContext.setGamePlayRequestEntity(gamePlayRequestEntity);
        gamePlayContext.setRequest(gamePlayRequestObject);

        SW.start("gameplay.persist.findorcreates");
        User user = null;
        Game game = null;
        Platform platform = null;

        if (gamePlayRequestObject.getPlayerId() != null) {
            user = userRepository.findByApiToken(gamePlayRequestObject.getPlayerId());
            /*Domain domain = domainRepository.findOrCreateByName(gamePlayRequestObject.getPlayerId().split("/")[0], () -> new Domain());
            user = userRepository.findOrCreateByGuid(gamePlayRequestObject.getPlayerId(), () -> User.builder().domain(domain).build());*/
        }
        if (gamePlayRequestObject.getGameKey() != null) {
            game = gameRepository.findOrCreateByGuid(gamePlayRequestObject.getGameKey(), () -> new Game());
        }
        if (gamePlayRequestObject.getPlatform() != null) {
            platform = platformRepository.findOrCreateByCode(gamePlayRequestObject.getPlatform(), () -> new Platform());
        }
        SW.stop();

        final User finalUser = user;
        final Game finalGame = game;
        final Platform finalPlatform = platform;

        SW.start("gameplay.persist.findgameplayround");
        GamePlay gamePlayEntity = null;
        try {
            gamePlayEntity = gamePlayRepository.findOrCreateByGuidAlwaysLock(
                    gamePlayRequestObject.getGamePlayId(),
                    () -> GamePlay.builder()
                            .guid(gamePlayRequestObject.getGamePlayId())
                            .game(finalGame)
                            .user(finalUser)
                            .platform(finalPlatform)
                            .roxorStatus(GamePlay.RoxorStatus.STARTED)
                            .build());
        } catch (CannotAcquireLockException e) {
            log.error("gamePlayPersist : " + ExceptionMessageUtil.allMessages(e), e);
            throw new Status400BadRequestException();
        } finally {
            SW.stop();
        }

        gamePlayContext.setGamePlayEntity(gamePlayEntity);
        SW.start("gameplay.persist.correlategameplay");
        gamePlayContext.getGamePlayRequestEntity().setGamePlay(gamePlayEntity);
        gamePlayContext.setGamePlayRequestEntity(gamePlayRequestRepository.save(gamePlayContext.getGamePlayRequestEntity()));
        SW.stop();

        gamePlayContext.setOperationEntityList(new ArrayList<>());

        List<Operation> existingOperationEntityList = operationRepository.findByGamePlayOrderByIdAsc(gamePlayEntity);
        gamePlayContext.setExistingOperationEntityList(existingOperationEntityList);

        Operation.Status defaultStatus = Operation.Status.REQUESTED;

        if(isRoundCompleted(gamePlayEntity)) {
            log.debug("GamePlay already in a finished state: {}", gamePlayContext);
            gamePlayRequestEntity.setStatus(GamePlayRequest.Status.ERROR);
            gamePlayRequestEntity.setStatusReason("GamePlay already in a finished state");
            gamePlayRequestRepository.save(gamePlayRequestEntity);

            defaultStatus = Operation.Status.IGNORE;
        }

        SW.start("gameplay.persist.operation.list");
        for (GamePlayOperation gamePlayOperation : gamePlayRequestObject.getOperations()) {
            OperationType operationType = operationTypeRepository.findByCode(gamePlayOperation.getOperationType().name());

            Operation persistOperation = Operation.builder()
                    .gamePlay(gamePlayEntity)
                    .gamePlayRequest(gamePlayRequestEntity)
                    .operationType(operationType)
                    .status(defaultStatus)
                    .build();

            switch (gamePlayOperation.getOperationType()) {
                case START_GAME_PLAY: {
                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case FINISH_GAME_PLAY: {
                    LifecycleFinishOperation lifecycleFinishOperation = (LifecycleFinishOperation) gamePlayOperation;
                    gamePlayContext.setRoxorFinishPresent(Boolean.TRUE);
                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case TRANSFER: {
                    TransferOperation transferOperation = (TransferOperation) gamePlayOperation;
                    Type transferType = typeRepository.findByCode(transferOperation.getType().name());
                    persistOperation.setTransferId(transferOperation.getTransferId());
                    persistOperation.setType(transferType);
                    persistOperation.setAmountCents(transferOperation.getAmount().getAmount());
                    persistOperation.setCurrency(currencyRepository.findOrCreateByCode(
                            transferOperation.getAmount().getCurrency(),
                            () -> Currency.builder()
                                    .code(transferOperation.getAmount().getCurrency())
                                    .build()));

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }

                    break;
                }
                case CANCEL_TRANSFER: {
                    TransferCancelOperation transferCancelOperation = (TransferCancelOperation) gamePlayOperation;
                    Type transferCancelType = typeRepository.findByCode(transferCancelOperation.getType().name());
                    persistOperation.setTransferId(transferCancelOperation.getTransferId());
                    persistOperation.setType(transferCancelType);
                    persistOperation.setAmountCents(transferCancelOperation.getAmount().getAmount());
                    persistOperation.setCurrency(currencyRepository.findOrCreateByCode(transferCancelOperation.getAmount().getCurrency(), () -> new Currency()));
                    gamePlayContext.setTransferCancelExists(Boolean.TRUE);

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case ACCRUAL: {
                    AccrualOperation accrualOperation = (AccrualOperation) gamePlayOperation;
                    persistOperation.setPoolId(accrualOperation.getPoolId());
                    persistOperation.setAccrualId(accrualOperation.getAccrualId());
                    persistOperation.setReference(accrualOperation.getReference());
                    persistOperation.setAmountCents(accrualOperation.getAmount().getAmount());
                    persistOperation.setCurrency(currencyRepository.findOrCreateByCode(accrualOperation.getAmount().getCurrency(), () -> new Currency()));

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case CANCEL_ACCRUAL: {
                    AccrualCancelOperation accrualCancelOperation = (AccrualCancelOperation) gamePlayOperation;
                    persistOperation.setPoolId(accrualCancelOperation.getPoolId());
                    persistOperation.setAccrualId(accrualCancelOperation.getAccrualId());
                    persistOperation.setReference(accrualCancelOperation.getReference());
                    persistOperation.setAmountCents(accrualCancelOperation.getAmount().getAmount());
                    persistOperation.setCurrency(currencyRepository.findOrCreateByCode(accrualCancelOperation.getAmount().getCurrency(), () -> new Currency()));

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case FREE_PLAY: {
                    RewardOperation rewardOperation = (RewardOperation) gamePlayOperation;
                    persistOperation.setTransferId(rewardOperation.getTransferId());
                    persistOperation.setAmountCents(rewardOperation.getAmount().getAmount());
                    persistOperation.setSource(sourceRepository.findOrCreateByGuid(
                            rewardOperation.getSource().getSourceId(),
                            () -> Source.builder()
                                    .guid(rewardOperation.getSource().getSourceId())
                                    .sourceType(rewardOperation.getSource().getSourceType())
                                    .build()));
                    persistOperation.setCurrency(currencyRepository.findOrCreateByCode(rewardOperation.getAmount().getCurrency(), () -> new Currency()));

                    populateRewardInfo(gamePlayContext, rewardOperation.getSource().getSourceId());

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
                case CANCEL_FREE_PLAY: {
                    RewardCancelOperation rewardCancelOperation = (RewardCancelOperation) gamePlayOperation;
                    persistOperation.setTransferId(rewardCancelOperation.getTransferId());
                    if (rewardCancelOperation.getAmount() != null) {
                        persistOperation.setAmountCents(rewardCancelOperation.getAmount().getAmount());
                        persistOperation.setCurrency(currencyRepository.findOrCreateByCode(rewardCancelOperation.getAmount().getCurrency(), () -> new Currency()));
                    }
                    persistOperation.setSource(sourceRepository.findOrCreateByGuid(
                            rewardCancelOperation.getSource().getSourceId(),
                            () -> Source.builder()
                                    .guid(rewardCancelOperation.getSource().getSourceId())
                                    .sourceType(rewardCancelOperation.getSource().getSourceType())
                                    .build()));
                    gamePlayContext.setFreePlayCancelExists(Boolean.TRUE);

                    populateRewardInfo(gamePlayContext, rewardCancelOperation.getSource().getSourceId());

                    Operation duplicateOperation = findExistingOperation(
                            existingOperationEntityList, persistOperation, gamePlayOperation.getOperationType());

                    if (duplicateOperation != null) {
                        persistOperation.setStatus(Operation.Status.DUPLICATE);
                        persistOperation.setAssociatedOperation(duplicateOperation);
                    }
                    break;
                }
            }

            persistOperation = operationRepository.save(persistOperation);
            gamePlayContext.getOperationEntityList().add(persistOperation);
        }
        SW.stop();
    }

    private void populateRewardInfo(GamePlayContext gamePlayContext, String sourceId)
    throws Status400BadRequestException
    {
        try {
            PlayerRewardTypeHistory playerRewardTypeHistory = queryRewardClientService.findByRewardTypeReference(sourceId);
            gamePlayContext.setPlayerRewardTypeHistory(playerRewardTypeHistory);
            log.info("PlayerRewardTypeHistory: "+playerRewardTypeHistory);

            RewardBonusMap rewardBonusMap = rewardBonusMapRepository.findByRoxorRewardId(sourceId);
            gamePlayContext.setRewardBonusMap(rewardBonusMap);
        } catch (Exception e) {
            log.error("Error retrieving reward info from svc-reward", e);
            throw new Status400BadRequestException();
        }
    }

    private boolean isRoundCompleted(GamePlay gamePlayEntity) {
        return gamePlayEntity.getRoxorStatus().equals(GamePlay.RoxorStatus.FINISHED);
    }

    public Operation findExistingOperation(
            List<Operation> existingOperationList,
            Operation checkDuplicateOperation,
            OperationTypeEnum duplicateOperationOperationType) {

        for (Operation operation : existingOperationList) {
            switch (duplicateOperationOperationType) {
                case FREE_PLAY:
                case CANCEL_FREE_PLAY:
                    if (checkDuplicateOperation.getTransferId() != null) {
                        if (checkDuplicateOperation.getTransferId().equals(operation.getTransferId())
                                && checkDuplicateOperation.getOperationType().getCode().equals(operation.getOperationType().getCode())
                                && operation.getStatus().equals(Operation.Status.RESULT)) {
                            return operation;
                        }
                    }
                    break;
                case TRANSFER:
                case CANCEL_TRANSFER:
                    if (checkDuplicateOperation.getTransferId() != null) {
                        if (checkDuplicateOperation.getTransferId().equals(operation.getTransferId())
                                && checkDuplicateOperation.getType().getCode().equals(operation.getType().getCode())
                                && checkDuplicateOperation.getOperationType().getCode().equals(operation.getOperationType().getCode())
                                && operation.getStatus().equals(Operation.Status.RESULT)) {
                            return operation;
                        }
                    }
                    break;
                case ACCRUAL:
                case CANCEL_ACCRUAL:
                    if (checkDuplicateOperation.getAccrualId() != null) {
                        if (checkDuplicateOperation.getAccrualId().equals(operation.getAccrualId())
                                && checkDuplicateOperation.getOperationType().getCode().equals(operation.getOperationType().getCode())
                                && operation.getStatus().equals(Operation.Status.RESULT)) {
                            return operation;
                        }
                    }
                    break;
                case START_GAME_PLAY:
                case FINISH_GAME_PLAY:
                    if (checkDuplicateOperation.getOperationType().getCode().equals(operation.getOperationType().getCode())
                            && operation.getStatus().equals(Operation.Status.RESULT)) {
                        return operation;
                    }
                    break;
            }
        }

        return null;
    }

}
