package lithium.service.casino.provider.roxor.services.gameplay;

import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status405UserDisabledException;
import lithium.metrics.SW;
import lithium.modules.ModuleInfo;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status401NotLoggedInException;
import lithium.service.casino.provider.roxor.api.exceptions.Status406DisabledGameException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.SuccessResponse;
import lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.GamePlayRequest;
import lithium.service.casino.provider.roxor.api.schema.gameplay.OperationTypeEnum;
import lithium.service.casino.provider.roxor.api.schema.gameplay.RewardCancelOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.RewardOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TransferCancelOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TransferOperation;
import lithium.service.casino.provider.roxor.api.schema.gameplay.TypeEnum;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.GamePlay;
import lithium.service.casino.provider.roxor.storage.entities.Operation;
import lithium.service.casino.provider.roxor.storage.entities.RewardBonusMap;
import lithium.service.casino.provider.roxor.storage.repositories.GamePlayRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationRepository;
import lithium.service.casino.provider.roxor.storage.repositories.OperationTypeRepository;
import lithium.service.casino.provider.roxor.storage.repositories.RewardBonusMapRepository;
import lithium.service.casino.provider.roxor.storage.repositories.TypeRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.exceptions.Status484WeeklyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status485WeeklyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status492DailyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status493MonthlyLossLimitReachedException;
import lithium.service.limit.client.exceptions.Status494DailyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status495MonthlyWinLimitReachedException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.reward.client.QueryRewardClientService;
import lithium.service.reward.client.dto.PlayerRewardTypeHistory;
import lithium.service.user.client.exceptions.Status438PlayTimeLimitReachedException;
import lithium.service.user.client.exceptions.Status500UserInternalSystemClientException;
import lithium.service.user.client.objects.LoginEvent;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class GamePlayPhase2Validate {
    @Autowired @Setter ValidationHelper validationHelper;
    @Autowired @Setter LimitInternalSystemService limits;
    @Autowired @Setter UserApiInternalClientService userApiInternalClientService;
    @Autowired @Setter GamePlayRepository gamePlayRepository;
    @Autowired @Setter OperationRepository operationRepository;
    @Autowired @Setter OperationTypeRepository operationTypeRepository;
    @Autowired @Setter TypeRepository typeRepository;
    @Autowired @Setter ModuleInfo moduleInfo;

    @Autowired @Setter QueryRewardClientService queryRewardClientService;
    @Autowired @Setter RewardBonusMapRepository rewardBonusMapRepository;

    public void validateGamePlayRequest(
            GamePlayContext context,
            String sessionKey,
            String gamePlayId,
            String xForwardFor,
            String locale
    ) throws
        Status400BadRequestException,
        Status401UnAuthorisedException,
        Status500RuntimeException,
        Status401NotLoggedInException,
        Status406DisabledGameException,
        Status512ProviderNotConfiguredException,
        Status500UserInternalSystemClientException,
        Status405UserDisabledException,
        Status496PlayerCoolingOffException,
        Status490SoftSelfExclusionException,
        Status500LimitInternalSystemClientException,
        Status484WeeklyLossLimitReachedException,
        Status485WeeklyWinLimitReachedException,
        Status491PermanentSelfExclusionException,
        Status493MonthlyLossLimitReachedException,
        Status492DailyLossLimitReachedException,
        Status495MonthlyWinLimitReachedException,
        Status494DailyWinLimitReachedException,
        Status478TimeSlotLimitException,
        Status438PlayTimeLimitReachedException {
        SW.start("gameplay.validate");
        GamePlayRequest gamePlayRequest = context.getRequest();
        context.setGamePlayId(gamePlayId);
        context.setSessionKey(sessionKey);
        context.setLocale(locale);

        try {
            if (gamePlayId == null) {
                context.setGamePlayRequestErrorReason("GamePlayId Not Provided In Header");
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest == null) {
                context.setGamePlayRequestErrorReason("Invalid JSON Body provided");
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest.getPlayerId() == null) {
                context.setGamePlayRequestErrorReason("No PlayerID provided in Body");
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest.getWebsite() == null) {
                context.setGamePlayRequestErrorReason("No Website provided in Body");
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest.getGameKey() == null) {
                context.setGamePlayRequestErrorReason("No GameKey provided in Body");
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest.getGamePlayId() == null) {
                context.setGamePlayRequestErrorReason("No GamePlayID provided in Body");
                throw new Status400BadRequestException(context);
            }
            //validate header supplied gamePlayId matches body supplied gamePlayId
            if (!gamePlayRequest.getGamePlayId().equalsIgnoreCase(gamePlayId)) {
                context.setGamePlayRequestErrorReason("Header GamePlayID : " + gamePlayId + " does not match GamePlayID in body : " + gamePlayRequest.getGamePlayId());
                throw new Status400BadRequestException(context);
            }
            if (gamePlayRequest.getOperations() == null || gamePlayRequest.getOperations().isEmpty()) {
                context.setGamePlayRequestErrorReason("No GamePlay-Operations provided in Body");
                throw new Status400BadRequestException(context);
            }
            //validate sessionKey is present in case TransferDebit operation is supplied
            if (!verifySessionKeyForOperations(sessionKey, gamePlayRequest.getOperations())) {
                context.setGamePlayRequestErrorReason("No SessionKey provided in Header when Transfer Debit exists in GamePlay-Operations provider in Body");
                throw new Status400BadRequestException(context);
            }
            //validate all operations contain a valid operationType
            if (!verifyValidOperationTypes(gamePlayRequest.getOperations())) {
                context.setGamePlayRequestErrorReason("GamePlay Operation List entry contains an unsupported OperationType");
                throw new Status400BadRequestException(context);
            }
            //validate all transfer operations contain a valid type debit / credit
            if (!verifyTransferOperationTypeValues(gamePlayRequest.getOperations())) {
                context.setGamePlayRequestErrorReason("GamePlay Operation List Transfer entry contains an unsupported Type");
                throw new Status400BadRequestException(context);
            }
            //validate all transfer operations contain a transfer id
            if (!verifyAllTransferOperationContainTransferID(gamePlayRequest.getOperations())) {
                context.setGamePlayRequestErrorReason("GamePlay Operation List Transfer entry does not contain a transferId");
                throw new Status400BadRequestException(context);
            }
            //validate that the gamePlay - round is not in a finished state
            if (context.getGamePlayEntity().getRoxorStatus().equals(GamePlay.RoxorStatus.FINISHED)
                    && context.getOperationEntityList().stream().anyMatch(o -> o.getStatus().equals(Operation.Status.REQUESTED))
            ) {
                context.setGamePlayRequestErrorReason("GamePlay already in a finished state");
                throw new Status400BadRequestException(context);
            }

            //validate transfer debit does not have a preceding transfer cancel in place
            if (verifyTransferDebitDoesNotHavePrecedingTransferCancel(context)) {
                context.setGamePlayRequestErrorReason("GamePlay Operation List contains a Transfer Cancel preceding Transfer.");
                throw new Status400BadRequestException(context);
            }

            if (context.getTransferCancelExists()) {
                //validate in the event that the a Transfer Cancel is supplied that the original transfer is either
                //in the same payload or exists as an operation linked to the same round
                //amounts and currency must also be identical between the original transfer and transfer cancel
                if (!verifyTransferCancelCorrelation(context)) {
                    throw new Status400BadRequestException(context);
                }

                //validate that the transfer cancel is not attempting to cancel a transfer-debit after a credit has already
                //been rewarded
                if (verifyTransferCancelNoCredit(context)) {
                    context.setGamePlayRequestErrorReason("GamePlay Transfer Cancel operation not allowed due to a credit awarded post the original debit");
                    throw new Status400BadRequestException(context);
                }
            }

            if (context.getFreePlayCancelExists()) {
                //validate in the event that the a Free Play Cancel is supplied that the original free play is either
                //in the same payload or exists as an operation linked to the same round
                //amounts, currency and source must also be identical between the original free play and free play cancel
                if (!verifyFreePlayCancelCorrelation(context)) {
                    throw new Status400BadRequestException(context);
                }
            }

            context.setFreePlayExists(verifyFreePlayOperationExists(context));
            context.setRoxorTransferDebitExists(verifyTransferDebitExists(context));

            if (context.getRoxorFinishPresent()) {
                context.setRoxorFinishWinPresent(verifyCreditOperationExists(context));
            }

            String userGuid = validationHelper.getUserGuidFromApiToken(gamePlayRequest.getPlayerId());
            if (userGuid == null) {
                throw new Status401NotLoggedInException();
            }
            String domainName = validationHelper.getDomainNameFromPlayerGuid(userGuid);

            // FIXME: Is session key unique per login event/aka session? Looks like it is.
            //        But if not, we have a problem here.
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.findLastLoginEventForSessionKey");
            LoginEvent lastLoginEvent = validationHelper.findLastLoginEventForSessionKey(context, sessionKey);
            SW.stop();
            //validate user session matches authenticate request user if login event found for sessionKey
            //validate the website supplied matches the provider config website
            //validate IP whitelist linked to xForwardFor http header
            validationHelper.validate(
                    context,
                    lastLoginEvent,
                    userGuid,
                    gamePlayRequest.getWebsite(),
                    xForwardFor
            );
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.getDomain");
            Domain domain = validationHelper.getDomain(context, domainName);
            SW.stop();
            //validate the user is enabled and able to play
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.performUserChecks");
            if(lastLoginEvent != null) {
                userApiInternalClientService.performUserChecks(userGuid, locale, lastLoginEvent.getId(),
                        true, true, false);
            } else {
                // e.g. https://jira.livescore.com/browse/PLAT-2825 <-- no sessionKey is sent, don't try and validate it.
                userApiInternalClientService.performUserChecks(userGuid, locale, null, true,
                        false, false);
            }
            SW.stop();
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.validatePlayTime");
            limits.validatePlayTime(userGuid);
            SW.stop();
            //validate there are no restrictions placed on the player
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.checkPlayerRestrictions");
            limits.checkPlayerRestrictions(userGuid, locale);
            SW.stop();
            //validate no limits have been breached by the player
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.checkLimits");
            limits.checkLimits(
                    domain.getName(),
                    userGuid,
                    domain.getCurrency(),
                    getTotalBetAmountInCents(gamePlayRequest.getOperations()),
                    locale
            );
            SW.stop();
            SW.start("gamePlayPhase2Validate.validateGamePlayRequest.getGame");
            lithium.service.games.client.objects.Game lithiumGame = validationHelper.getGame(
                    context,
                    domain.getName(),
                    moduleInfo.getModuleName(),
                    gamePlayRequest.getGameKey()
            );
            SW.stop();

            context.setLithiumGame(lithiumGame);
            context.setRequest(gamePlayRequest);
            context.setUserGuid(userGuid);
            context.setUserApiToken(gamePlayRequest.getPlayerId());
            context.setResponse(SuccessResponse.builder().build());
            context.setLoginEvent(lastLoginEvent);
            context.setDomain(domain);
        } finally {
            SW.stop();
        }
    }

    private Boolean verifySessionKeyForOperations(String sessionKey, List<? extends GamePlayOperation> operations) {
        Boolean transferDebitExists = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.TRANSFER))
                .anyMatch(o -> ((TransferOperation) o).getType().equals(TypeEnum.DEBIT));

        if (transferDebitExists) {
            return sessionKey != null ? Boolean.TRUE : Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private Boolean verifyAllTransferOperationContainTransferID(List<? extends GamePlayOperation> operations) {

        Boolean transferIdCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.TRANSFER))
                .anyMatch(o -> ((TransferOperation) o).getTransferId() == null);

        if (transferIdCheck) return Boolean.FALSE;

        transferIdCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.CANCEL_TRANSFER))
                .anyMatch(o -> ((TransferCancelOperation) o).getTransferId() == null);

        if (transferIdCheck) return Boolean.FALSE;

        transferIdCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.FREE_PLAY))
                .anyMatch(o -> ((RewardOperation) o).getTransferId() == null);

        if (transferIdCheck) return Boolean.FALSE;

        transferIdCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.CANCEL_FREE_PLAY))
                .anyMatch(o -> ((RewardCancelOperation) o).getTransferId() == null);

        if (transferIdCheck) return Boolean.FALSE;

        return Boolean.TRUE;
    }

    private Boolean verifyTransferCancelCorrelation(GamePlayContext context) {
        Boolean verified = context.getOperationEntityList().stream()
                .filter(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.CANCEL_TRANSFER.name()))
                .allMatch(cancelTransferOperation -> {
                    if (cancelTransferOperation.getAmountCents() == null) {
                            context.setGamePlayRequestErrorReason("Cancel Transfer operation does not contain an amount");
                            return Boolean.FALSE;
                        }

                        //verify transfer not in list else look up in db
                        Operation transferOperationInList = context.getOperationEntityList().stream()
                                .filter(transferOperation -> transferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name()))
                                .filter(transferOperation -> transferOperation.getTransferId().equalsIgnoreCase(cancelTransferOperation.getTransferId()) &&
                                        transferOperation.getType().getCode().equals(cancelTransferOperation.getType().getCode()))
                                .filter(transferOperation->transferOperation.getStatus().equals(Operation.Status.RESULT))
                                .findFirst().orElse(null);

                        if (transferOperationInList != null) {
                            //set correlation
                            cancelTransferOperation.setAssociatedOperation(transferOperationInList);

                            if (!transferOperationInList.getAmountCents().equals(cancelTransferOperation.getAmountCents())) {
                                context.setGamePlayRequestErrorReason("Cancel Transfer and Transfer Amounts Mismatch");
                                return Boolean.FALSE;
                            }

                            if (!transferOperationInList.getCurrency().getCode().equals(cancelTransferOperation.getCurrency().getCode())) {
                                context.setGamePlayRequestErrorReason("Cancel Transfer and Transfer Amount Currency Mismatch");
                                return Boolean.FALSE;
                            }
                        } else {
                            Operation transferOperationInDb = context.getExistingOperationEntityList().stream()
                                    .filter(transferOperation -> transferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name()))
                                    .filter(transferOperation -> transferOperation.getTransferId().equalsIgnoreCase(cancelTransferOperation.getTransferId()))
                                    .filter(transferOperation->transferOperation.getStatus().equals(Operation.Status.RESULT))
                                    .findFirst().orElse(null);

                            if (transferOperationInDb == null) {
                                cancelTransferOperation.setStatus(Operation.Status.IGNORE);
                                return Boolean.TRUE;
                            }

                            //set correlation
                            cancelTransferOperation.setAssociatedOperation(transferOperationInDb);

                            if (!transferOperationInDb.getType().getCode().equalsIgnoreCase(cancelTransferOperation.getType().getCode())) {
                                context.setGamePlayRequestErrorReason("Cancel Transfer and Transfer Type Mismatch");
                                return Boolean.FALSE;
                            }

                            if (!transferOperationInDb.getAmountCents().equals(cancelTransferOperation.getAmountCents())) {
                                context.setGamePlayRequestErrorReason("Cancel Transfer and Transfer Amounts Mismatch");
                                return Boolean.FALSE;
                            }

                            if (!transferOperationInDb.getCurrency().getCode().equals(cancelTransferOperation.getCurrency().getCode())) {
                                context.setGamePlayRequestErrorReason("Cancel Transfer and Transfer Amount Currency Mismatch");
                                return Boolean.FALSE;
                            }

                            // PLAT-1157 / LSPLAT-443 - Transfer Cancel received for Transfer Debit in Error State
                            // Ignore processing as wallet result should be 0
                            // return success to roxor to prevent stuck state
                            if (transferOperationInDb.getStatus().equals(Operation.Status.ERROR)) {
                                cancelTransferOperation.setStatus(Operation.Status.IGNORE);
                                return Boolean.TRUE;
                            }
                        }

                        return Boolean.TRUE;
                    }
                );

        //persist updates to db and refresh context
        context.setOperationEntityList(operationRepository.saveAll(context.getOperationEntityList()));

        return verified;
    }

    private Boolean verifyFreePlayCancelCorrelation(GamePlayContext context) {
        Boolean verified = context.getOperationEntityList().stream()
                .filter(cancelFreePlayOperation -> cancelFreePlayOperation.getOperationType().getCode().equals(OperationTypeEnum.CANCEL_FREE_PLAY.name()))
                .allMatch(cancelFreePlayOperation -> {
                            if (cancelFreePlayOperation.getAmountCents() == null) {
                                context.setGamePlayRequestErrorReason("Cancel Free Play operation does not contain an amount");
                                return Boolean.FALSE;
                            }

                            if (cancelFreePlayOperation.getSource() == null) {
                                context.setGamePlayRequestErrorReason("Cancel Free Play operation does not contain a source");
                                return Boolean.FALSE;
                            }

                            //verify free play not in list else look up in db
                            Operation freePlayOperationInList = context.getOperationEntityList().stream()
                                    .filter(freePlayOperation -> freePlayOperation.getOperationType().getCode().equals(OperationTypeEnum.FREE_PLAY.name()))
                                    .filter(freePlayOperation -> freePlayOperation.getTransferId().equalsIgnoreCase(cancelFreePlayOperation.getTransferId()))
                                    .findFirst().orElse(null);

                            if (freePlayOperationInList != null) {
                                //set correlation
                                cancelFreePlayOperation.setAssociatedOperation(freePlayOperationInList);

                                if (!freePlayOperationInList.getAmountCents().equals(cancelFreePlayOperation.getAmountCents())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Amounts Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInList.getCurrency().getCode().equals(cancelFreePlayOperation.getCurrency().getCode())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Amount Currency Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInList.getSource().getSourceType().equalsIgnoreCase(cancelFreePlayOperation.getSource().getSourceType())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Source Type Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInList.getSource().getGuid().equalsIgnoreCase(cancelFreePlayOperation.getSource().getGuid())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Source ID Mismatch");
                                    return Boolean.FALSE;
                                }
                            } else {
                                Operation freePlayOperationInDb = context.getExistingOperationEntityList().stream()
                                        .filter(freePlayOperation -> freePlayOperation.getOperationType().getCode().equals(OperationTypeEnum.FREE_PLAY.name()))
                                        .filter(freePlayOperation -> freePlayOperation.getTransferId().equalsIgnoreCase(cancelFreePlayOperation.getTransferId()))
                                        .findFirst().orElse(null);

                                if (freePlayOperationInDb == null) {
                                    cancelFreePlayOperation.setStatus(Operation.Status.IGNORE);
                                    return Boolean.TRUE;
                                }

                                //set correlation
                                cancelFreePlayOperation.setAssociatedOperation(freePlayOperationInDb);

                                if (!freePlayOperationInDb.getAmountCents().equals(cancelFreePlayOperation.getAmountCents())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Amounts Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInDb.getCurrency().getCode().equals(cancelFreePlayOperation.getCurrency().getCode())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Amount Currency Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInDb.getSource().getSourceType().equalsIgnoreCase(cancelFreePlayOperation.getSource().getSourceType())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Source Type Mismatch");
                                    return Boolean.FALSE;
                                }

                                if (!freePlayOperationInDb.getSource().getGuid().equalsIgnoreCase(cancelFreePlayOperation.getSource().getGuid())) {
                                    context.setGamePlayRequestErrorReason("Cancel Free Play and Free Play Source ID Mismatch");
                                    return Boolean.FALSE;
                                }
                            }

                            return Boolean.TRUE;
                        }
                );

        //persist updates to db and refresh context
        context.setOperationEntityList(operationRepository.saveAll(context.getOperationEntityList()));

        return verified;
    }

    private Boolean verifyTransferCancelNoCredit(GamePlayContext context) {

        Boolean transferCancelDebitExists = context.getOperationEntityList().stream()
                .anyMatch(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.CANCEL_TRANSFER.name())
                        && cancelTransferOperation.getType().getCode().equals(TypeEnum.DEBIT.name()));

        if (transferCancelDebitExists) {
            Boolean creditExists = context.getExistingOperationEntityList().stream()
                    .anyMatch(o -> o.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name())
                            && o.getType().getCode().equals(TypeEnum.CREDIT.name()));

            return creditExists;
        }

        return Boolean.FALSE;
    }

    private Boolean verifyValidOperationTypes(List<? extends GamePlayOperation> operations) {
        Boolean verifyOperationTypes = operations.stream().allMatch(o -> o.getOperationType() != null);

        return verifyOperationTypes;
    }

    private Boolean verifyTransferOperationTypeValues(List<? extends GamePlayOperation> operations) {
        Boolean typeCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.TRANSFER))
                .anyMatch(o -> ((TransferOperation) o).getType() == null);

        if (typeCheck) return Boolean.FALSE;

        typeCheck = operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.CANCEL_TRANSFER))
                .anyMatch(o -> ((TransferCancelOperation) o).getType() == null);

        if (typeCheck) return Boolean.FALSE;

        return Boolean.TRUE;

    }

    private Long getTotalBetAmountInCents(List<? extends GamePlayOperation> operations) {
        return operations.stream()
                .filter(o -> o.getOperationType().equals(OperationTypeEnum.TRANSFER))
                .filter(o -> ((TransferOperation) o).getType().equals(TypeEnum.DEBIT))
                .mapToLong(o -> ((TransferOperation) o).getAmount().getAmount())
                .reduce(0, Long::sum);
    }

    private Boolean verifyCreditOperationExists(GamePlayContext context) {

        Boolean creditOperationFound = context.getOperationEntityList().stream()
                .anyMatch(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name())
                        && cancelTransferOperation.getType().getCode().equals(TypeEnum.CREDIT.name()));

        if (creditOperationFound) {
            return Boolean.TRUE;
        }

        creditOperationFound = context.getExistingOperationEntityList().stream()
                .anyMatch(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name())
                        && cancelTransferOperation.getType().getCode().equals(TypeEnum.CREDIT.name()));

        return creditOperationFound;
    }

    private Boolean verifyTransferDebitExists(GamePlayContext context) {
        Boolean transferDebitOperationFound = context.getOperationEntityList().stream()
                .anyMatch(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name())
                        && cancelTransferOperation.getType().getCode().equals(TypeEnum.DEBIT.name()));

        if (transferDebitOperationFound) {
            return Boolean.TRUE;
        }

        transferDebitOperationFound = context.getExistingOperationEntityList().stream()
                .anyMatch(cancelTransferOperation -> cancelTransferOperation.getOperationType().getCode().equals(OperationTypeEnum.TRANSFER.name())
                        && cancelTransferOperation.getType().getCode().equals(TypeEnum.DEBIT.name())
                        && !cancelTransferOperation.getStatus().equals(Operation.Status.ERROR));

        return transferDebitOperationFound;
    }

    private Boolean verifyTransferDebitDoesNotHavePrecedingTransferCancel(GamePlayContext context) {

        Operation transferDebitOperation = context.getOperationEntityList().stream()
                .filter(o -> o.getOperationType().getCode().equalsIgnoreCase(OperationTypeEnum.TRANSFER.name())
                    && o.getType().getCode().equalsIgnoreCase(TypeEnum.DEBIT.name()))
                .findFirst().orElse(null);

        if (transferDebitOperation == null) {
            return Boolean.FALSE;
        }

        Operation transferDebitCancelOperation = context.getExistingOperationEntityList().stream()
                .filter(o -> o.getOperationType().getCode().equalsIgnoreCase(OperationTypeEnum.CANCEL_TRANSFER.name())
                        && o.getTransferId().equalsIgnoreCase(transferDebitOperation.getTransferId())
                        && o.getType().getCode().equalsIgnoreCase(transferDebitOperation.getType().getCode()))
                .findFirst().orElse(null);

        if (transferDebitCancelOperation != null) {
            transferDebitOperation.setAssociatedOperation(transferDebitCancelOperation);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }


    private Boolean verifyFreePlayOperationExists(GamePlayContext context) {
        Operation freePlayOperationInRequest = context.getOperationEntityList().stream()
            .filter(freePlayOperation -> freePlayOperation.getOperationType().getCode().equals(OperationTypeEnum.FREE_PLAY.name()))
            .findAny().orElse(null);

        if (freePlayOperationInRequest != null) {
            context.setFreePlayOperation(freePlayOperationInRequest);
            return Boolean.TRUE;
        }

        Operation freePlayOperationInDB = context.getExistingOperationEntityList().stream()
            .filter(freePlayOperation -> freePlayOperation.getOperationType().getCode().equals(OperationTypeEnum.FREE_PLAY.name())
                && !freePlayOperation.getStatus().equals(Operation.Status.ERROR))
            .findAny().orElse(null);

        if (freePlayOperationInDB != null) {
            if (freePlayOperationInDB.getSource() != null && freePlayOperationInDB.getSource().getGuid() != null) {
                try {
                    PlayerRewardTypeHistory playerRewardTypeHistory = queryRewardClientService.findByRewardTypeReference(
                        freePlayOperationInDB.getSource().getGuid());
                    context.setPlayerRewardTypeHistory(playerRewardTypeHistory);

                    RewardBonusMap rewardBonusMap = rewardBonusMapRepository.findByRoxorRewardId(freePlayOperationInDB.getSource().getGuid());
                    context.setRewardBonusMap(rewardBonusMap);
                } catch (Exception e) {
                    log.error("Error retrieving reward info from svc-reward", e);
                    return Boolean.FALSE;
                }
            }

            context.setFreePlayOperation(freePlayOperationInDB);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }
}
