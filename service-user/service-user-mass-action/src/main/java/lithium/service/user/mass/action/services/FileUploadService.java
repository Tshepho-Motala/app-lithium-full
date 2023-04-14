package lithium.service.user.mass.action.services;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.mass.action.api.backoffice.schema.FileDataSummaryResponse;
import lithium.service.user.mass.action.api.backoffice.schema.ActionsRequest;
import lithium.service.user.mass.action.api.backoffice.schema.ProgressResponse;
import lithium.service.user.mass.action.data.entities.Action;
import lithium.service.user.mass.action.data.entities.ActionType;
import lithium.service.user.mass.action.data.entities.DataError;
import lithium.service.user.mass.action.data.entities.FileMeta;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.DBFile;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.data.entities.User;
import lithium.service.user.mass.action.data.repositories.ActionRepository;
import lithium.service.user.mass.action.data.repositories.FileDataRepository;
import lithium.service.user.mass.action.data.repositories.FileMetaRepository;
import lithium.service.user.mass.action.data.repositories.FileUploadRepository;
import lithium.service.user.mass.action.exceptions.Status400FileStorageException;
import lithium.service.user.mass.action.exceptions.Status404DataRecordNotFoundException;
import lithium.service.user.mass.action.exceptions.Status404MassActionNotFoundException;
import lithium.service.user.mass.action.exceptions.Status422DataValidationError;
import lithium.service.user.mass.action.helpers.CSVHelper;
import lithium.service.user.mass.action.stream.uservalidation.UserUploadProcessingTriggerStream;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class FileUploadService {

    @Autowired LithiumTokenUtilService tokenService;
    @Autowired FileUploadRepository fileUploadRepository;
    @Autowired FileDataRepository fileDataRepository;
    @Autowired FileMetaRepository fileMetaRepository;
    @Autowired DBFileStorageService dbFileStorageService;
    @Autowired ActionRepository actionRepository;
    @Autowired DomainService domainService;
    @Autowired MassActionService massActionService;
    @Autowired UserService userService;
    @Autowired MassValidationService massValidationService;
    @Autowired
    UserUploadProcessingTriggerStream userUploadProcessingTriggerStream;
    @Autowired CachingDomainClientService cachingDomainClientService;

    @TimeThisMethod
    public FileUpload uploadBonusCSV(String domainName, MultipartFile file, Principal principal, String bonusCode, Double defaultAmount, Boolean allowDuplicates, String bonusDescription, UploadType uploadType) {
        SW.start("saveFileMeta");
        FileMeta fileMeta = fileMetaRepository.save(FileMeta.builder()
                .bonusCode(bonusCode)
                .defaultBonusAmount(defaultAmount)
                .allowDuplicates(allowDuplicates)
                .bonusDescription(bonusDescription)
                .build());
        SW.stop();

        SW.start("saveAction");
        //FIXME: actions should be dictated from backoffice
        actionRepository.save(Action.builder()
                .name(ActionType.GRANT_BONUS)
                .massActionMeta(fileMeta)
                .build());
        SW.stop();

        SW.start("saveFileUpload");
        FileUpload bonusFileUpload = FileUpload.builder()
                .uploadStatus(UploadStatus.UPLOADED)
                .uploadDate(new Date())
                .uploadType(uploadType)
                .author(userService.findOrCreate(tokenService.getUser(principal).guid()))
                .domain(domainService.findOrCreate(domainName))
                .recordsFound(0)
                .massActionMeta(fileMeta)
                .build();

        //Setting Foreign keys
        fileMeta.setFileUploadMeta(bonusFileUpload);
        bonusFileUpload = fileUploadRepository.save(bonusFileUpload);
        SW.stop();

        try {
            //Checking for parsing errors by running through file without processing the records (ie. no inserts). RecordsFound is used by progress bar
            SW.start("validateCSV");
            int validRecordsFound = CSVHelper.validateCSV(file);
            SW.stop();
            SW.start("saveDBFile");
            DBFile dbFile = dbFileStorageService.storeFile(file);
            SW.stop();
            log.debug("CSV extracted: Number of records found: {}, file-id: {}", validRecordsFound, dbFile.getId());

            bonusFileUpload.setRecordsFound(validRecordsFound);
            bonusFileUpload.setFile(dbFile);
            bonusFileUpload.setUploadStatus(UploadStatus.UPLOADED);

            //Setting Foreign keys
            dbFile.setFileUploadMeta(bonusFileUpload);
            SW.start("saveFileUpload");
            FileUpload newFileUpload =  fileUploadRepository.save(bonusFileUpload);
            SW.stop();

            SW.start("triggerUploadProcessingStream");
            userUploadProcessingTriggerStream.trigger(newFileUpload.getId());
            SW.stop();
            return newFileUpload;

        } catch (Status422DataValidationError validationError) {
            log.error("File validation error on file: " + validationError.getMessage());
            bonusFileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(bonusFileUpload);
        } catch (IOException ioException) {
            log.error("Failed to open csv file: " + ioException.getMessage());
            bonusFileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(bonusFileUpload);
        } catch (Status400FileStorageException fileStorageException) {
            log.error("Could not store file, kindly check your file: " + fileStorageException.getMessage());
            bonusFileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(bonusFileUpload);
        }
    }

    @TimeThisMethod
    public FileUpload uploadPlayerCSV(String domainName, MultipartFile file, ActionsRequest actionsRequest) {
        SW.start("saveFileMeta");
        FileMeta fileMeta = fileMetaRepository.save(FileMeta.builder().build());
        SW.stop();

        SW.start("saveFileUpload");
        FileUpload fileUpload = fileUploadRepository.save(FileUpload.builder()
                .uploadStatus(UploadStatus.UPLOADED)
                .uploadDate(new Date())
                .uploadType(UploadType.PLAYER_INFO)
                .author(userService.findOrCreate(actionsRequest.getAuthorGuid()))
                .domain(domainService.findOrCreate(domainName))
                .massActionMeta(fileMeta)
                .recordsFound(0)
                .build());
        SW.stop();

        //Setting Foreign keys
        fileMeta.setFileUploadMeta(fileUpload);
        SW.start("saveFileMeta");
        fileMetaRepository.save(fileMeta);
        SW.stop();

        try {
            //Checking for parsing errors by running through file without processing the records (ie. no inserts). RecordsFound is used by progress bar
            SW.start("validateCSV");
            int validRecordsFound = CSVHelper.validateCSV(file);
            SW.stop();
            SW.start("saveDBFile");
            DBFile dbFile = dbFileStorageService.storeFile(file);
            SW.stop();
            log.debug("CSV extracted: Number of records found: {}, file-id: {}", validRecordsFound, dbFile.getId());

            fileUpload.setRecordsFound(validRecordsFound);
            fileUpload.setFile(dbFile);
            fileUpload.setUploadStatus(UploadStatus.UPLOADED);


            //create a stream here

            //Setting Foreign keys
            dbFile.setFileUploadMeta(fileUpload);

            SW.start("saveFileUpload");
            fileUploadRepository.save(fileUpload);
            SW.stop();

            SW.start("triggerUploadProcessingStream");
            userUploadProcessingTriggerStream.trigger(fileUpload.getId());
            SW.stop();

            return fileUpload;
        } catch (Status422DataValidationError validationError) {
            log.error("File validation error on file: " + validationError.getMessage());
            fileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(fileUpload);
        } catch (IOException ioException) {
            log.error("Failed to open csv file: " + ioException.getMessage());
            fileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(fileUpload);
        } catch (Status400FileStorageException fileStorageException) {
            log.error("Could not store file, kindly check your file: " + fileStorageException.getMessage());
            fileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            return fileUploadRepository.save(fileUpload);
        }
    }

    public ProgressResponse getFileUploadProgress(String domainName, String stageName, Long fileUploadId) {
        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(fileUploadId);

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get();

            switch (stageName) {
                case "user-verification": {
                    double totalCheckingElements = fileDataRepository.countFileDataByFileUploadMeta(fileUpload);
                    double percentile = new BigDecimal(totalCheckingElements * 100 / fileUpload.getRecordsFound()).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                    return ProgressResponse.builder()
                            .percentile(percentile)
                            .uploadStatus(fileUpload.getUploadStatus())
                            .build();
                }
                default: {
                    switch (fileUpload.getUploadType()) {
                        case PLAYER_INFO: return getPlayerProcessingProgress(fileUpload);
                        case BONUS_CASH: return getBonusProcessingProgress(fileUpload);
                        case BONUS_FREESPIN:
                        case BONUS_INSTANT:
                        case BONUS_CASINOCHIP:
                            return getBonusProcessingProgress(fileUpload);
                        default:return ProgressResponse.builder()
                                .percentile(0)
                                .uploadStatus(fileUpload.getUploadStatus())
                                .build();
                    }
                }
            }


        }
        return ProgressResponse.builder()
                .percentile(0)
                .build();
    }

    @TimeThisMethod
    public ProgressResponse getPlayerProcessingProgress(FileUpload fileUpload) {

        if (fileUpload.getUploadStatus().equals(UploadStatus.PROCESSING) || fileUpload.getUploadStatus().equals(UploadStatus.DONE)) {
            double recordsFound = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatusNotNull(fileUpload, Arrays.asList(UploadStatus.CHECKED, UploadStatus.DONE, UploadStatus.FAILED_STAGE_1, UploadStatus.FAILED_STAGE_2));
            double totalProcessedElements = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatusNotNull(fileUpload, Arrays.asList(UploadStatus.DONE, UploadStatus.FAILED_STAGE_2));
            double percentile = totalProcessedElements != 0 && recordsFound != 0 ? new BigDecimal(totalProcessedElements * 100 / recordsFound).setScale(2, BigDecimal.ROUND_DOWN).doubleValue() : 0;
            return ProgressResponse.builder()
                    .percentile(percentile)
                    .uploadStatus(fileUpload.getUploadStatus())
                    .build();
        }
        return ProgressResponse.builder()
                .percentile(0)
                .uploadStatus(fileUpload.getUploadStatus())
                .build();
    }

    @TimeThisMethod
    public ProgressResponse getBonusProcessingProgress(FileUpload fileUpload) {

        if (fileUpload.getUploadStatus().equals(UploadStatus.PROCESSING) || fileUpload.getUploadStatus().equals(UploadStatus.DONE)) {
            double recordsFound;
            double totalProcessedElements;
            if (fileUpload.getMassActionMeta().isAllowDuplicates()) {
                recordsFound = fileDataRepository.countFileDataByFileUploadMetaAndUploadStatusInAndUserStatus(fileUpload, Arrays.asList(UploadStatus.CHECKED, UploadStatus.DONE, UploadStatus.FAILED_STAGE_1), Status.OPEN);
                totalProcessedElements = fileDataRepository.countFileDataByFileUploadMetaAndUploadStatusInAndUserStatus(fileUpload, Arrays.asList(UploadStatus.DONE, UploadStatus.FAILED_STAGE_2), Status.OPEN);
            } else {
                recordsFound = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatus(fileUpload, Arrays.asList(UploadStatus.CHECKED, UploadStatus.DONE, UploadStatus.FAILED_STAGE_1), Status.OPEN);
                totalProcessedElements = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatus(fileUpload, Arrays.asList(UploadStatus.DONE, UploadStatus.FAILED_STAGE_2), Status.OPEN);
            }
            double percentile = totalProcessedElements != 0 && recordsFound != 0 ? new BigDecimal(totalProcessedElements * 100 / recordsFound).setScale(2, BigDecimal.ROUND_DOWN).doubleValue() : 0;
            return ProgressResponse.builder()
                    .percentile(percentile)
                    .uploadStatus(fileUpload.getUploadStatus())
                    .build();
        }

        return ProgressResponse.builder()
                .percentile(0)
                .uploadStatus(null)
                .build();
    }

    @TimeThisMethod
    public FileUpload getFileUploadStatus(Long id) {

        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(id);

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get().getFile().getFileUploadMeta();
            List<Action> actions = actionRepository.findAllByMassActionMeta(fileUpload.getMassActionMeta());
            fileUpload.getMassActionMeta().setActions(actions);
            return fileUpload;
        }
        return null;
    }

    @TimeThisMethod
    public Page<FileData> getFileData(Long fileUploadId, DataTableRequest request) {

        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(fileUploadId);

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get();

            if (fileUpload.getUploadStatus().equals(UploadStatus.CHECKED)
                || fileUpload.getUploadStatus().equals(UploadStatus.PROCESSING)
                || fileUpload.getUploadStatus().equals(UploadStatus.DONE)
                || fileUpload.getUploadStatus().equals(UploadStatus.FAILED_STAGE_1)
                || fileUpload.getUploadStatus().equals(UploadStatus.FAILED_STAGE_2)) {
                return fileDataRepository.findAllByFileUploadMeta(fileUpload, request.getPageRequest());
            }
        }
        return new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);
    }

    @TimeThisMethod
    public FileUpload processBonusFileUpload(ActionsRequest request) throws Status404MassActionNotFoundException {
        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(request.getFileUploadId());

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get();

            if (fileUpload.getUploadStatus().equals(UploadStatus.CHECKED)) {
                FileMeta fileUploadMeta = fileMetaRepository.findByFileUploadMeta(fileUpload);
                fileUploadMeta.setBonusCode(request.getBonusCode());
                fileUploadMeta.setDefaultBonusAmount(request.getDefaultBonusAmount());
                fileUploadMeta.setBonusDescription(request.getBonusDescription());
                fileUploadMeta.setAllowDuplicates(request.isAllowDuplicates());

                fileMetaRepository.save(fileUploadMeta);
                fileUpload.setUploadStatus(UploadStatus.PROCESSING);

                FileUpload saveUpload = fileUploadRepository.save(fileUpload);
                Long id = saveUpload.getId();

                // Executing this in a runnable to prevent ultra-long waits.
                // This code was referenced from lithium/metrics/MetricsExporter.java
                ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("fileupload-massaction-cronexecutor-%d").build();
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(factory);
                executorService.schedule(new Runnable() {
                    @Override
                    public void run() {
                        //Cron Job manual trigger
                        massActionService.run(saveUpload.getUploadType(), id);
                    }
                },0, TimeUnit.MILLISECONDS);

                return  saveUpload;
            } else if (fileUpload.getUploadStatus().equals(UploadStatus.UPLOADED)) {
                throw new Status404MassActionNotFoundException("Bonus File is still being uploaded, try again later.");
            } else if (fileUpload.getUploadStatus().equals(UploadStatus.CHECKING)) {
                throw new Status404MassActionNotFoundException("Bonus File is currently being checked, try again later");
            }
            throw new Status404MassActionNotFoundException("Bonus File has already been submitted for processing, check status on process file upload service.");
        }
        throw new Status404MassActionNotFoundException("Bonus file upload not found");
    }

    @TimeThisMethod
    public FileUpload processPlayerFileUpload(ActionsRequest actionsRequest) throws Status404MassActionNotFoundException {
        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(actionsRequest.getFileUploadId());

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get();

            if (fileUpload.getUploadStatus().equals(UploadStatus.CHECKED)) {
                FileMeta fileMeta = massActionService.findOrCreateFileMetaWithActions(actionsRequest, fileUpload);
                fileMeta.setStatus(Status.fromName(actionsRequest.getStatus()));
                fileMeta.setStatusReason(StatusReason.fromName(actionsRequest.getStatusReason()));
                fileMeta.setStatusComment(actionsRequest.getStatusComment());
                fileMeta.setVerificationStatusId(actionsRequest.getVerificationStatus());
                fileMeta.setVerificationStatusComment(actionsRequest.getVerificationStatusComment());
                fileMeta.setAgeVerified(actionsRequest.getAgeVerified());
                fileMeta.setAddressVerified(actionsRequest.getAddressVerified());
                fileMeta.setBiometricsStatus(actionsRequest.getBiometricsStatus());
                fileMeta.setBiometricsStatusComment(actionsRequest.getBiometricsStatusComment());
                fileMeta.setTestPlayer(actionsRequest.isTestPlayer());
                fileMeta.setAddTags(actionsRequest.getAddTagsSetToString());
                fileMeta.setReplaceTagFrom(actionsRequest.getReplaceTagFrom());
                fileMeta.setReplaceTagTo(actionsRequest.getReplaceTagTo());
                fileMeta.setRemoveTags(actionsRequest.getRemoveTagsSetToString());
                fileMeta.setNoteCategory(actionsRequest.getNoteCategory());
                fileMeta.setNoteSubCategory(actionsRequest.getNoteSubCategory());
                fileMeta.setNotePriority(actionsRequest.getNotePriority());
                fileMeta.setNoteComment(actionsRequest.getNoteComment());
                fileMeta.setAdjustmentAmountCents(actionsRequest.getAdjustmentAmountCents());
                fileMeta.setAdjustmentTransactionTypeCode(actionsRequest.getAdjustmentTransactionTypeCode());
                fileMeta.setAdjustmentComment(actionsRequest.getAdjustmentComment());
                fileMeta.setPlayerRestrictions(actionsRequest.getPlayerRestrictions());
                fileMeta.setAccessRule(actionsRequest.getAccessRule());

                fileMetaRepository.save(fileMeta);
                fileUpload.setUploadStatus(UploadStatus.PROCESSING);
                fileUploadRepository.save(fileUpload);

                ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("fileupload-massaction-cronexecutor-%d").build();
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(factory);
                executorService.schedule(() -> massActionService.run(UploadType.PLAYER_INFO, fileUpload.getId()),0, TimeUnit.MILLISECONDS);

                return fileUpload;

            } else if (fileUpload.getUploadStatus().equals(UploadStatus.UPLOADED)) {
                throw new Status404MassActionNotFoundException("File is still being uploaded, try again later.");
            } else if (fileUpload.getUploadStatus().equals(UploadStatus.CHECKING)) {
                throw new Status404MassActionNotFoundException("File is currently being checked, try again later");
            }
            throw new Status404MassActionNotFoundException("File has already been submitted for processing, check status on process file upload service.");
        }
        throw new Status404MassActionNotFoundException("File upload not found");
    }

    @TimeThisMethod
    public FileDataSummaryResponse getBonusFileUploadSummary(Long id) {

        Optional<FileUpload> fileUpload = fileUploadRepository.findById(id);

        if (fileUpload.isPresent()) {
            FileUpload bonusFileUpload = fileUpload.get();
            long existingNotFailedPlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatusAndUploadStatusNot(bonusFileUpload, Status.OPEN, UploadStatus.FAILED_STAGE_1);
            long existingPlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatus(bonusFileUpload, Status.OPEN);
            long duplicatePlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateTrueAndUserStatus(bonusFileUpload, Status.OPEN);
            long undefinedPlayers = fileDataRepository.countFileDataByFileUploadMetaAndUserStatusNot(bonusFileUpload, Status.OPEN);
            return FileDataSummaryResponse.builder()
                    .existingPlayers(existingPlayers)
                    .undefinedPlayers(undefinedPlayers)
                    .existingNotFailedPlayers(existingNotFailedPlayers)
                    .duplicatePlayers(duplicatePlayers)
                    .build();
            }
        return FileDataSummaryResponse.builder()
                .existingPlayers(0)
                .undefinedPlayers(0)
                .duplicatePlayers(0)
                .build();
    }

    @TimeThisMethod
    public FileDataSummaryResponse getPlayerFileUploadSummary(Long id) {

        Optional<FileUpload> optionalFileUpload = fileUploadRepository.findById(id);

        if (optionalFileUpload.isPresent()) {
            FileUpload fileUpload = optionalFileUpload.get();
            long existingNotFailedPlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusNotAndUserStatusNotNull(fileUpload, UploadStatus.FAILED_STAGE_1);
            long existingPlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatusNotNull(fileUpload);
            long duplicatePlayers = fileDataRepository.countFileDataByFileUploadMetaAndDuplicateTrueAndUserStatusNotNull(fileUpload);
            long undefinedPlayers = fileDataRepository.countFileDataByFileUploadMetaAndUserStatusIsNull(fileUpload);
            return FileDataSummaryResponse.builder()
                    .existingPlayers(existingPlayers)
                    .undefinedPlayers(undefinedPlayers)
                    .existingNotFailedPlayers(existingNotFailedPlayers)
                    .duplicatePlayers(duplicatePlayers)
                    .build();
        }
        return FileDataSummaryResponse.builder()
                .existingPlayers(0)
                .undefinedPlayers(0)
                .duplicatePlayers(0)
                .build();
    }

    public void removeFileDataRecord(Long fileUploadId, Long rowNumber) throws Status404DataRecordNotFoundException, Status404MassActionNotFoundException {

        Optional<FileUpload> fileUpload = fileUploadRepository.findById(fileUploadId);
        if (fileUpload.isPresent()) {
            Optional<FileData> record = fileDataRepository.findByFileUploadMetaAndRowNumber(fileUpload.get(), rowNumber);
            if(record.isPresent()) {
                fileDataRepository.delete(record.get());
                Optional<FileData> first = fileDataRepository.findFirstByFileUploadMetaAndPlayer(fileUpload.get(), record.get().getPlayer());
                if(first.isPresent()) {
                    FileData fileData = first.get();
                    fileData.setDuplicate(false);
                    fileDataRepository.save(fileData);
                }
            } else {
                throw new Status404DataRecordNotFoundException("Unable to delete row-number=" + rowNumber);
            }
        } else {
            throw new Status404MassActionNotFoundException("Mass action not found: Unable to delete row-number=" + rowNumber);
        }
    }

    public Page<FileUpload> findFileUploads(String domainName, Principal principal, UploadType uploadType, DataTableRequest request) {
        User author = userService.findOrCreate(tokenService.getUser(principal).guid());

        //LSPLAT-3130_PLAT-3857_admin_super_user_with_MASS_PLAYER_UPDATE_VIEW_role_should_be_able_to_see_all_jobs_including_the_ones_he_did_not_create
        if (tokenService.getUser(principal).hasRole("ADMIN")) {
            return fileUploadRepository.findFileUploadByDomainNameAndUploadType(domainName, uploadType, request.getPageRequest());
        } else {
            return fileUploadRepository.findFileUploadByAuthorAndDomainNameAndUploadType(author, domainName, uploadType, request.getPageRequest());
        }
    }

    /**
     * Do bonus validation on file data record by making use of the file meta information and makes changes to the fileData object
     */
    public void handleBonusValidation(FileData fileData, FileUpload fileUpload, UploadStatus uploadStatus) {
        log.debug("handle bonus validations - check amount provided else apply default");
        FileMeta fileMeta = fileUpload.getMassActionMeta();

        //Checks the amount and apply default bonus amount if not set
        if (fileData.getAmount() == 0) {
            if (fileMeta.getDefaultBonusAmount() != null && fileMeta.getDefaultBonusAmount() > 0) {
                fileData.setAmount(fileMeta.getDefaultBonusAmount());
                fileData.setAppliedDefaultAmount(true);
                fileData.clearDataError();
                fileData.setUploadStatus(UploadStatus.CHECKED);
            }
            if (fileMeta.getDefaultBonusAmount() != null && fileMeta.getDefaultBonusAmount() == 0 && fileUpload.getUploadType().equals(UploadType.BONUS_CASH)) {
                fileData.setDataError(DataError.DEFAULT_AMOUNT_NOT_PROVIDED);
                fileData.setUploadStatus(uploadStatus);
            }

        } else {
            String domainMaximumPayoutSetting = cachingDomainClientService.getDomainSetting(fileUpload.getDomain().getName(), DomainSettings.MAXIMUM_BONUS_PAYOUT);
            Double domainMaximumPayout = domainMaximumPayoutSetting != null && !domainMaximumPayoutSetting.isEmpty()
                  ? Double.valueOf(domainMaximumPayoutSetting)
                  : Double.valueOf(DomainSettings.MAXIMUM_BONUS_PAYOUT.defaultValue());
            if (fileData.getAmount() > domainMaximumPayout) { //If amount does not pass validation check
                fileData.setDataError(DataError.INVALID_AMOUNT_PROVIDED);
                fileData.setUploadStatus(uploadStatus);
            }
        }
    }

    public List<FileUpload> retrieveFileUpload(UploadStatus uploadStatus, UploadType uploadType) {
        return fileUploadRepository.findFileUploadByUploadStatusAndUploadType(uploadStatus, uploadType);
    }

    public Optional<FileUpload> retrieveFileUploadById(Long fileUploadId) {
        return fileUploadRepository.findFileUploadById(fileUploadId);
    }

    public void updateFileUploadStatus(UploadStatus uploadStatus, FileUpload fileUpload) {
        fileUpload.setUploadStatus(uploadStatus);
        fileUploadRepository.save(fileUpload);
    }

    public List<FileData> retrieveFileDataForGrantMassBonusJob(FileUpload fileupload) {
        List<UploadStatus> uploadStatuses = Arrays.asList(UploadStatus.CHECKED, UploadStatus.FAILED_STAGE_1);
            return fileDataRepository.findAllByFileUploadMetaAndUploadStatusInAndUserStatus(fileupload, uploadStatuses, Status.OPEN);
    }

    public List<FileData> retrieveFileDataByUploadStatusAndDuplicateFalse(FileUpload fileUpload, UploadStatus uploadStatus) {
        return fileDataRepository.findAllByFileUploadMetaAndDuplicateFalseAndUploadStatus(fileUpload, uploadStatus);
    }
}
