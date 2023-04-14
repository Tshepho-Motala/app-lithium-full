package lithium.service.user.mass.action.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLog;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.math.CurrencyAmount;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.Response;
import lithium.service.access.client.AuthorizationClient;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.AuthorizationResult;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.SystemBonusClientService;
import lithium.service.casino.client.data.BonusAllocatev2;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.objects.UserRestrictionSet;
import lithium.service.limit.client.objects.UserRestrictionsRequest;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.objects.AccountCode;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserAccountStatusUpdate;
import lithium.service.user.client.objects.UserBiometricsStatusUpdate;
import lithium.service.user.client.objects.UserVerificationStatusUpdate;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.service.user.mass.action.api.backoffice.schema.ActionsRequest;
import lithium.service.user.mass.action.data.entities.Action;
import lithium.service.user.mass.action.data.entities.ActionType;
import lithium.service.user.mass.action.data.entities.DataError;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileMeta;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.data.repositories.ActionRepository;
import lithium.service.user.mass.action.data.repositories.FileDataRepository;
import lithium.service.user.mass.action.data.repositories.FileMetaRepository;
import lithium.service.user.mass.action.data.repositories.FileUploadRepository;
import lithium.service.user.mass.action.objects.RestrictionData;
import lithium.service.user.mass.action.stream.processing.MassUserProcessingTriggerStream;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static lithium.cashier.CashierTransactionLabels.COMMENT_LABEL;

@Slf4j
@Service
public class MassActionService {

    private static final String PLAYER_BALANCE = "PLAYER_BALANCE";
    private static final String BALANCE_ADJUST = "BALANCE_ADJUST";
    private static final String MANUAL_BALANCE_ADJUST = "MANUAL_BALANCE_ADJUST";
    private static final String MASS_ACTION_COMMENT_SUFFIX = " (Mass Player Update job)";

    @Autowired
    FileUploadRepository fileUploadRepository;
    @Autowired
    FileDataRepository fileDataRepository;
    @Autowired
    FileMetaRepository fileMetaRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    FileUploadService fileUploadService;
    @Autowired
    SystemBonusClientService systemBonusClientService;
    @Autowired
    UserApiInternalClientService userApiInternalClientService;
    @Autowired
    ChangeLogService changeLogService;
    @Autowired
    LimitInternalSystemService limitInternalSystemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    LithiumServiceClientFactory services;

    @Autowired
    private MassUserProcessingTriggerStream massUserProcessingTrigger;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;

    @Autowired
    private MessageSource messageSource;

    public void run(UploadType uploadType) {
        SW.start("retrieveFileUpload");
        List<FileUpload> fileUploads = fileUploadService.retrieveFileUpload(UploadStatus.PROCESSING, uploadType);
        SW.stop();

        for (FileUpload fileUpload : fileUploads) {
            if (fileUpload.getMassActionMeta() == null) {
                continue; // No actions to perform
            }

            SW.start("findAllByMassActionMeta");
            List<Action> actions = actionRepository.findAllByMassActionMeta(fileUpload.getMassActionMeta());
            SW.stop();

            SW.start("getFileDataByUploadType");
            List<FileData> fileDataList = getFileDataByUploadType(fileUpload, uploadType);
            SW.stop();

            for (FileData fileData : fileDataList) {
                SW.start("performActionsOnFileData");
                performActionsOnFileData(fileUpload, fileData, actions);
                SW.stop();
                if (fileUpload.getUploadType().equals(UploadType.PLAYER_INFO)) {
                    fileData.setUploadStatus(UploadStatus.DONE);
                }
                SW.start("saveFileData");
                fileDataRepository.save(fileData);
                SW.stop();
            }
            SW.start("updateFileUploadStatus");
            fileUploadService.updateFileUploadStatus(UploadStatus.DONE, fileUpload);
            SW.stop();
        }
    }

    @TimeThisMethod
    public void run(UploadType uploadType, Long fileUploadId) {
        SW.start("retrieveFileUploadById");
        Optional<FileUpload> fileUploadResults = fileUploadService.retrieveFileUploadById(fileUploadId);
        SW.stop();
        if(fileUploadResults.isPresent()){
            FileUpload fileUpload = fileUploadResults.get();
            if (fileUpload.getMassActionMeta() == null) {
                return;
            }

            SW.start("getFileDataByUploadType");
            List<FileData> fileDataList = getFileDataByUploadType(fileUpload, uploadType);
            SW.stop();

            for (FileData fileData : fileDataList) {
                SW.start("massUserProcessingTrigger");
                massUserProcessingTrigger.trigger(fileData);
                SW.stop();
            }
        }

    }

    private List<FileData> getFileDataByUploadType(FileUpload fileUpload, UploadType uploadType) {
        switch (uploadType) {
            case BONUS_CASH:
            case BONUS_FREESPIN:
            case BONUS_CASINOCHIP:
            case BONUS_INSTANT:
                return fileUploadService.retrieveFileDataForGrantMassBonusJob(fileUpload);
            case PLAYER_INFO:
                return fileUploadService.retrieveFileDataByUploadStatusAndDuplicateFalse(fileUpload, UploadStatus.CHECKED);
            default: return new ArrayList<>();
        }
    }

    @TimeThisMethod
    public void performActionsOnFileData(FileUpload fileUpload, FileData fileData, List<Action> actions) {
        for (Action action : actions) {
            performActionOnFileData(fileUpload, fileData, action);
        }
    }

    @TimeThisMethod
    private void performActionOnFileData(FileUpload fileUpload, FileData fileData, Action action) {
        switch (action.getName()) {
            case GRANT_BONUS:
                SW.start("mass-action-grantMassBonus");
                grantMassBonus(fileUpload, fileData);
                SW.stop();
                break;
            case CHANGE_STATUS:
                SW.start("mass-action-changeStatus");
                changeStatus(fileUpload, fileData);
                SW.stop();
                break;
            case CHANGE_VERIFICATION_STATUS:
                SW.start("mass-action-changeVerificationStatus");
                changeVerificationStatus(fileUpload, fileData);
                SW.stop();
                break;
            case MARK_AS_TEST_PLAYER:
                SW.start("mass-action-markAsTestPlayer");
                markAsTestPlayer(fileUpload, fileData);
                SW.stop();
                break;
            case ADD_PLAYER_TAGS:
                SW.start("mass-action-addPlayerTags");
                addPlayerTags(fileUpload, fileData);
                SW.stop();
                break;
            case REPLACE_PLAYER_TAGS:
                SW.start("mass-action-replacePlayerTags");
                replacePlayerTags(fileUpload, fileData);
                SW.stop();
                break;
            case REMOVE_ALL_PLAYER_TAGS:
                SW.start("mass-action-removeAllPlayerTags");
                removeAllPlayerTags(fileUpload, fileData);
                SW.stop();
                break;
            case REMOVE_PLAYER_TAGS:
                SW.start("mass-action-removePlayerTags");
                removePlayerTags(fileUpload, fileData);
                SW.stop();
                break;
            case ADD_NOTE:
                SW.start("mass-action-addNote");
                addNote(fileUpload, fileData);
                SW.stop();
                break;
            case BALANCE_ADJUSTMENT:
                SW.start("mass-action-balanceAdjustment");
                balanceAdjustment(fileUpload, fileData);
                SW.stop();
                break;
            case PROCESS_ACCESS_RULE:
                SW.start("mass-action-processAccessRule");
                processAccessRule(fileUpload, fileData);
                SW.stop();
                break;

            case LIFT_PLAYER_RESTRICTIONS:
            case PLACE_PLAYER_RESTRICTIONS:
                SW.start("mass-action-updatePlayerRestrictions");
                updatePlayerRestrictions(action.getName(), fileUpload, fileData);
                SW.stop();
                break;
            case CHANGE_BIOMETRICS_STATUS:
                SW.start("mass-action-updatePlayerBiometricsStatus");
                changeBiometricsStatus( fileUpload, fileData);
                SW.stop();
                break;
            default: break;
        }
    }
    private void processAccessRule(FileUpload fileUpload, FileData fileData) {
        try {
            AuthorizationResult result = getAuthorizationResult(fileUpload.getDomain().getName(), fileData.getPlayerGuid(), fileUpload.getMassActionMeta().getAccessRule());

            fileData.setRuleSetResultSuccess(result.isSuccessful());
            fileData.setRuleSetResultMessage(result.getMessage());

            log.info("The rule:" + fileUpload.getMassActionMeta().getAccessRule() +
                    " was applied to the user guid: " + fileData.getPlayerGuid() + " with message:" + result.getMessage());

        } catch (LithiumServiceClientFactoryException e) {
            logErrorOnAction(fileUpload, fileData, ActionType.PROCESS_ACCESS_RULE, e);
            log.error("an error occurred while applying the rule " + fileUpload.getMassActionMeta().getAccessRule() + "to the userGuid:" + fileData.getPlayerGuid() + ". Because:" + e.getMessage(), e);
        }
    }

    private AuthorizationResult getAuthorizationResult(String domainName, String userGuid, String accessRuleName) throws LithiumServiceClientFactoryException {
        AuthorizationClient authorizationClient = services.target(AuthorizationClient.class, "service-access", true);

        AuthorizationRequest request = AuthorizationRequest.builder()
                .userGuid(userGuid)
                .build();

        return authorizationClient.checkAuthorization(domainName, accessRuleName, request).getData();
    }

    private void logErrorOnAction(FileUpload fileUpload, FileData fileData, ActionType actionType, Exception e) {
        log.error("Unable to perform ActionType." + actionType.name() + " for " + fileData.getPlayerGuid() + " on upload-id=" + fileUpload.getId() + ": ErrorMessage: " + e.getMessage());
        switch (actionType) {
            case GRANT_BONUS: {
                fileData.setDataError(DataError.UNABLE_TO_GRANT_BONUS); //FIXME: This needs to handle multiple errors from actions in the future
                break;
            }
            case BALANCE_ADJUSTMENT:{
                fileData.setDataError(DataError.INVALID_AMOUNT_PROVIDED);
            }
        }
        fileData.setUploadStatus(UploadStatus.FAILED_STAGE_2);
    }

    @TimeThisMethod
    private void grantMassBonus(FileUpload fileUpload, FileData fileData) {
        SW.start("handleBonusValidation");
        fileUploadService.handleBonusValidation(fileData, fileUpload, UploadStatus.FAILED_STAGE_2);
        SW.stop();

        boolean grantBonus = true;
        if (fileData.isDuplicate() && !fileUpload.getMassActionMeta().isAllowDuplicates()) { grantBonus = false; }
        if (fileData.getDataError() != null) { grantBonus = false; }
        if (grantBonus) {
            BonusAllocatev2 bonusAllocatev2 = BonusAllocatev2.builder()
                    .bonusCode(fileUpload.getMassActionMeta().getBonusCode())
                    .description(fileUpload.getMassActionMeta().getBonusDescription())
                    .playerGuid(fileData.getPlayerGuid())
                    .build();
            switch (fileUpload.getUploadType()) {
                case BONUS_INSTANT:
                case BONUS_CASINOCHIP:
                case BONUS_FREESPIN:
                    if ((fileData.getAmount() != null) && (fileData.getAmount() > 0)) {
                        bonusAllocatev2.setCustomAmountNotMoney((CurrencyAmount.fromAmount(fileData.getAmount()).toAmount().intValue()));
                        bonusAllocatev2.setDescription("Custom Amount: " +bonusAllocatev2.getCustomAmountNotMoney()+" | "+bonusAllocatev2.getDescription());
                    } else {
                        bonusAllocatev2.setCustomAmountNotMoney(null);
                    }
                    break;
                default:
                    bonusAllocatev2.setCustomAmountDecimal(fileData.getAmount());
                    break;
            }
            try {
                SW.start("call massActionRegisterForCashBonus");
                systemBonusClientService.massActionRegisterForCashBonus(bonusAllocatev2);
            } catch (Exception e) {
                logErrorOnAction(fileUpload, fileData, ActionType.GRANT_BONUS, e);
            } finally {
                SW.stop();
            }
            fileData.setUploadStatus(UploadStatus.DONE);
        }
        fileDataRepository.save(fileData);
    }

    private void changeStatus(FileUpload fileUpload, FileData fileData) {
        try {
            userApiInternalClientService.changeAccountStatus(
                    UserAccountStatusUpdate.builder()
                            .userGuid(fileData.getPlayerGuid())
                            .authorGuid(fileUpload.getAuthorGuid())
                            .statusName(fileUpload.getMassActionMeta().getStatus().statusName())
                            .statusReasonName(fileUpload.getMassActionMeta().getStatusReason() != null ? fileUpload.getMassActionMeta().getStatusReason().statusReasonName() : null)
                            .comment(fileUpload.getMassActionMeta().getStatusComment())
                            .noteCategoryName(Category.ACCOUNT.getName())
                            .noteSubCategoryName(SubCategory.STATUS_CHANGE.getName())
                            .notePriority(70)
                            .build());
            fileData.setUserStatus(fileUpload.getMassActionMeta().getStatus());
            fileData.setUserStatusReason(fileUpload.getMassActionMeta().getStatusReason());
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Unable to update the user status for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.CHANGE_STATUS, e);
        }
    }

    private void changeVerificationStatus(FileUpload fileUpload, FileData fileData) {
        try {

            UserVerificationStatusUpdate userVerificationStatusUpdate = UserVerificationStatusUpdate.builder()
                    .authorName(fileUpload.getAuthorGuid())
                    .userId(fileData.getUploadedPlayerId())
                    .statusId(fileUpload.getMassActionMeta().getVerificationStatusId())
                    .ageVerified(fileUpload.getMassActionMeta().getAgeVerified())
                    .addressVerified(fileUpload.getMassActionMeta().getAddressVerified())
                    .comment(fileUpload.getMassActionMeta().getVerificationStatusComment())
                    .build();

            boolean forceUpdate =
                    fileUpload.getMassActionMeta().getAddressVerified() == null
                            && fileUpload.getMassActionMeta().getAgeVerified() == null;

            userApiInternalClientService.updateVerificationStatus(forceUpdate, userVerificationStatusUpdate);

        } catch (Exception e) {
            log.error("Unable to update the user verification status for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.CHANGE_STATUS, e);
        }
    }

    private void changeBiometricsStatus(FileUpload fileUpload, FileData fileData) {

        try {
            userApiInternalClientService.updateBiometricsStatus(
                    UserBiometricsStatusUpdate
                            .builder()
                            .userGuid(fileData.getPlayerGuid())
                            .biometricsStatus(fileUpload.getMassActionMeta().getBiometricsStatus())
                            .comment(fileUpload.getMassActionMeta().getBiometricsStatusComment() + MASS_ACTION_COMMENT_SUFFIX)
                            .authorGuid(fileUpload.getAuthorGuid())
                            .build()
            );

        } catch (Exception e) {
            log.error("Unable to update the user verification status for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.CHANGE_STATUS, e);
        }
    }

    private void markAsTestPlayer(FileUpload fileUpload, FileData fileData) {
        try {
            userApiInternalClientService.setTest(fileData.getUploadedPlayerId(), fileUpload.getMassActionMeta().getTestPlayer());
        } catch (Exception e) {
            log.error("Unable to update the test status for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.MARK_AS_TEST_PLAYER, e);
        }
    }

    private void addPlayerTags(FileUpload fileUpload, FileData fileData) {
        try {
            userApiInternalClientService.categoryAddPlayer(fileData.getUploadedPlayerId(), fileUpload.getMassActionMeta().getAddTags());
        } catch (Exception e) {
            log.error("Unable to add tags for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.ADD_PLAYER_TAGS, e);
        }
    }

    @TimeThisMethod
    private void replacePlayerTags(FileUpload fileUpload, FileData fileData) {
        try {
            SW.start("categoryRemovePlayer");
            userApiInternalClientService.categoryRemovePlayer(fileData.getUploadedPlayerId(), fileUpload.getMassActionMeta().getReplaceTagFrom().toString());
            SW.stop();

            SW.start("categoryAddPlayer");
            userApiInternalClientService.categoryAddPlayer(fileData.getUploadedPlayerId(), fileUpload.getMassActionMeta().getReplaceTagTo().toString());
            SW.stop();
        } catch (Exception e) {
            log.error("Unable to replace tags for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.REPLACE_PLAYER_TAGS, e);
        }
    }

    private void removeAllPlayerTags(FileUpload fileUpload, FileData fileData) {
        try {
            userApiInternalClientService.categoryRemoveAllPlayer(fileData.getUploadedPlayerId());
        } catch (Exception e) {
            log.error("Unable to remove all tags for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.REMOVE_ALL_PLAYER_TAGS, e);
        }
    }

    private void removePlayerTags(FileUpload fileUpload, FileData fileData) {
        try {
            userApiInternalClientService.categoryRemovePlayer(fileData.getUploadedPlayerId(), fileUpload.getMassActionMeta().getRemoveTags());
        } catch (Exception e) {
            log.error("Unable to remove tags for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.REMOVE_PLAYER_TAGS, e);
        }
    }

    private void addNote(FileUpload fileUpload, FileData fileData) {
        try {
            changeLogService.addNote(ChangeLog.builder()
                    .entityRecordId(fileData.getUploadedPlayerId())
                    .categoryName(fileUpload.getMassActionMeta().getNoteCategory())
                    .subCategoryName(fileUpload.getMassActionMeta().getNoteSubCategory())
                    .priority(fileUpload.getMassActionMeta().getNotePriority())
                    .comments(fileUpload.getMassActionMeta().getNoteComment())
                    .domainName(fileUpload.getDomain().getName())
                    .build());
        } catch (Exception e) {
            log.error("Unable to add note for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.ADD_NOTE, e);
        }
    }

    private void balanceAdjustment(FileUpload fileUpload, FileData fileData) {

        try {
            AccountingClient client = getAccountingClient();

            String currencyCode = cachingDomainClientService.getDefaultDomainCurrency(fileUpload.getDomain().getName());

            long amountInCents = BigDecimal.valueOf(fileData.getAmount()).movePointRight(2).longValue();

            String adjustmentTransactionTypeCode = fileUpload.getMassActionMeta().getAdjustmentTransactionTypeCode();
            
            if (!AccountCode.isValid(adjustmentTransactionTypeCode, amountInCents)) {
                log.error("Account code is not valid for this balance adjustment");
                fileData.setDataError(DataError.INVALID_ACCOUNT_CODE);
                fileData.setUploadStatus(UploadStatus.FAILED_STAGE_2);
                return;
            }

            String commentFromAction = COMMENT_LABEL + "=" + fileUpload.getMassActionMeta().getAdjustmentComment();

            Response<AdjustmentTransaction> adjustMultiResponse = client.adjustMulti(amountInCents,
                    new DateTime().toDateTimeISO().toString(),
                    PLAYER_BALANCE,
                    PLAYER_BALANCE,
                    BALANCE_ADJUST,
                    adjustmentTransactionTypeCode,
                    MANUAL_BALANCE_ADJUST,
                    new String[]{commentFromAction},
                    currencyCode,
                    fileUpload.getDomain().getName(),
                    fileData.getPlayerGuid(),
                    fileUpload.getAuthorGuid(),
                    false,
                    null);

            if (adjustMultiResponse.isSuccessful()) {
                saveChangeLogForBalanceAdjust(fileUpload, currencyCode, fileData.getAmount(), fileData, commentFromAction, fileUpload.getMassActionMeta().getAdjustmentTransactionTypeCode());
            }

        } catch (Exception e) {
            log.error("Unable to adjust balance for {}", fileData.getPlayerGuid());
            logErrorOnAction(fileUpload, fileData, ActionType.BALANCE_ADJUSTMENT, e);
        }
    }

    private void saveChangeLogForBalanceAdjust(FileUpload fileUpload, String currencyCode, double amount, FileData fileData, String commentFromAction, String transactionType) throws Status500InternalServerErrorException {
        try {

            List<ChangeLogFieldChange> clfc = new ArrayList<>();

            String comment = messageSource.getMessage("UI_NETWORK_ADMIN.USER.BALANCE.ADJUST",
                    new Object[]{
                            currencyCode,
                            amount,
                            transactionType,
                            commentFromAction
                    },
                    Locale.US
            );

            User externalAuthorUser = getExternalUser(fileUpload.getAuthorGuid());
            if (externalAuthorUser == null)
                throw new Status500InternalServerErrorException("Cant save changelog for mass action balance adjust. User author, not found for guid:" + fileUpload.getAuthorGuid());
            changeLogService.registerChangesWithDomainAndFullName(
                    "user",
                    "edit",
                    fileData.getUploadedPlayerId(),
                    externalAuthorUser.guid(),
                    comment,
                    null,
                    clfc,
                    Category.ACCOUNT,
                    SubCategory.EDIT_DETAILS,
                    0,
                    fileUpload.getDomain().getName(),
                    externalAuthorUser.getFirstName() + " " + externalAuthorUser.getLastName()
            );
        } catch (Exception e) {
            log.error("ChangeLogService could not register changes  ", e);
            throw new Status500InternalServerErrorException("changeLogService error");
        }
    }

    private User getExternalUser(String guid) throws Status500InternalServerErrorException {
        User externalUser = null;
        try {
            externalUser = userApiInternalClientService.getUserByGuid(guid);
        } catch (UserClientServiceFactoryException e) {
            throw new Status500InternalServerErrorException("userApiInternalClientService error", e);
        }
        return externalUser;
    }

    private AccountingClient getAccountingClient() {
        AccountingClient client = null;
        try {
            client = services.target(AccountingClient.class, "service-accounting", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error("Problem getting AccountingClient, " + e.getMessage(), e);
        }
        return client;
    }

    public Action findOrCreateAction(String actionName, FileMeta fileMeta) {

        Optional<Action> actionByName = actionRepository.findActionByMassActionMetaAndName(fileMeta, ActionType.fromName(actionName));

        if (actionByName.isPresent()) {
            return actionByName.get();
        }

        return actionRepository.save(Action.builder()
                .name(ActionType.fromName(actionName))
                .massActionMeta(fileMeta)
                .build());
    }

    public FileMeta findOrCreateFileMetaWithActions(ActionsRequest actionsRequest, FileUpload fileUpload) {
        Optional<FileMeta> fileMetaOptional = Optional.ofNullable(fileMetaRepository.findByFileUploadMeta(fileUpload));

        FileMeta fileMeta;
        if (fileMetaOptional.isPresent()) {
            fileMeta = fileMetaOptional.get();
        } else {
            fileMeta = fileMetaRepository.save(FileMeta.builder().build());
        }

        findOrCreateActions(fileMeta, actionsRequest.getActions());

        return fileMeta;
    }

    public void findOrCreateActions(FileMeta fileMeta, Set<String> actions) {
        if (actions != null) {
            actions.stream().map(action -> {
                return findOrCreateAction(action, fileMeta);
            }).collect(Collectors.toList());
        }
    }

    public void updatePlayerRestrictions(ActionType actionType, FileUpload fileUpload, FileData data) {
        String restrictions = fileUpload.getMassActionMeta().getPlayerRestrictions();

        if( restrictions != null) {

            try {
                RestrictionData restrictionData = mapper.readValue(restrictions, RestrictionData.class);

                if(restrictionData.getRestrictions() != null && !restrictionData.getRestrictions().isEmpty()) {
                    UserRestrictionsRequest request = UserRestrictionsRequest.builder()
                            .userGuid(data.getPlayerGuid())
                            .userId(data.getUploadedPlayerId())
                            .domainRestrictionSets(restrictionData.getRestrictions().stream().collect(Collectors.toList()))
                            .comment(restrictionData.getReason())
                            .subType(restrictionData.getSubType())
                            .build();

                    if(actionType == ActionType.LIFT_PLAYER_RESTRICTIONS) {
                        limitInternalSystemService.liftMany(request);
                    }
                    else if( actionType == ActionType.PLACE_PLAYER_RESTRICTIONS) {
                        Response<List<UserRestrictionSet>> response = limitInternalSystemService.setMany(request);
                        if(response != null && response.getData2() != null) {
                            Map<String, String> failedSet = mapper.convertValue(response.getData2(), new TypeReference<>() {});
                            if(!failedSet.isEmpty()) {
                                String comment = failedSet.entrySet().stream().map(entry -> entry.getKey() + "-" + entry.getValue()).collect(Collectors.joining(". "));
                                data.setComment(comment);
                                logErrorOnAction(fileUpload, data, actionType, new Exception(comment));
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
