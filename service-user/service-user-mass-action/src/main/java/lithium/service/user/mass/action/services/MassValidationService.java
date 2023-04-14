package lithium.service.user.mass.action.services;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.service.user.mass.action.api.backoffice.schema.FileDataSummaryResponse;
import lithium.service.user.mass.action.api.backoffice.schema.ProgressResponse;
import lithium.service.user.mass.action.data.entities.DataError;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.data.repositories.FileDataRepository;
import lithium.service.user.mass.action.data.repositories.FileUploadRepository;
import lithium.service.user.mass.action.exceptions.Status422DataValidationError;
import lithium.service.user.mass.action.helpers.CSVHelper;
import lithium.service.user.mass.action.helpers.CustomMultipartFile;
import lithium.service.user.mass.action.objects.UserValidation;
import lithium.service.user.mass.action.stream.uservalidation.UserValidationTriggerStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MassValidationService {

    private FileUploadRepository fileUploadRepository;
    private FileDataRepository fileDataRepository;
    private UserApiInternalClientService userApiInternalClientService;
    private FileUploadService fileUploadService;
    private UserService userService;
    private UserValidationTriggerStream userValidationTriggerStream;

    @TimeThisMethod
    public void run(UploadType uploadType) {
        List<FileUpload> fileUploads = fileUploadService.retrieveFileUpload(UploadStatus.UPLOADED, uploadType);
        for (FileUpload fileUpload : fileUploads) {
            fileUploadService.updateFileUploadStatus(UploadStatus.CHECKING, fileUpload);
            validateUser(fileUpload);
        }
    }

    @TimeThisMethod
    public void run(Long fileUploadId) {
        Optional<FileUpload> fileUploadResults = fileUploadService.retrieveFileUploadById(fileUploadId);
        if(fileUploadResults.isPresent()){
            FileUpload fileUpload = fileUploadResults.get();
            fileUploadService.updateFileUploadStatus(UploadStatus.CHECKING, fileUpload);
            validateUser(fileUpload);
        }
    }

    @TimeThisMethod
    private void validateUser(FileUpload fileUpload) {

        List<FileData> fileDataList = new ArrayList<>();
        SW.start("fileToFileData");
        try {
            fileDataList = CSVHelper.fileToFileData(new CustomMultipartFile(fileUpload.getFile().getData(), fileUpload.getFile().getFileName()));
        } catch (Status422DataValidationError | IOException validationError) {
            log.error("Failed to read uploaded file from DB, errorMessage: " + validationError.getMessage());
            fileUploadService.updateFileUploadStatus(UploadStatus.FAILED_STAGE_1, fileUpload);
        }
        SW.stop();

        SW.start("userValidationTriggerStream");
        fileDataList.stream().forEach(fileData -> {
            userValidationTriggerStream.processUploadedRecord(
                UserValidation.builder()
                    .fileUploadId(fileUpload.getId())
                    .rowNumber(fileData.getRowNumber())
                    .uploadedDomainName(fileUpload.getDomain().getName())
                    .uploadedPlayerId(fileData.getUploadedPlayerId())
                    .amount(fileData.getAmount())
                    .duplicate(fileData.isDuplicate())
                    .build()
            );
        });
        SW.stop();
    }

    private void postMassActionValidationChecks(FileUpload fileUpload) {
        FileDataSummaryResponse fileUploadSummary;
        switch (fileUpload.getUploadType()) {
            case PLAYER_INFO: {
                fileUploadSummary = fileUploadService.getPlayerFileUploadSummary(fileUpload.getId());
                break;
            }
            default: { // BONUS_CASH && BONUS_FREESPIN
                fileUploadSummary = fileUploadService.getBonusFileUploadSummary(fileUpload.getId());
                break;
            }
        }
        if (fileUploadSummary.getExistingPlayers() != 0) {
            // After successfully processing the file, change the uploadStatus to CHECKED so that backoffice are able to know that the file is ready for action
            fileUpload.setUploadStatus(UploadStatus.CHECKED);
        } else {
            // If there are no valid players to process
            fileUpload.setUploadStatus(UploadStatus.FAILED_STAGE_1);
        }
    }

    @TimeThisMethod
    private void doUserChecks(FileData fileData, String uploadedDomainName) {
        try {
            User user = userApiInternalClientService.getUserById(fileData.getUploadedPlayerId());
            String playerDomainName = user.guid().split("/")[0];

            if (playerDomainName.equalsIgnoreCase(uploadedDomainName)) {
                fileData.setPlayer(userService.findOrCreate(user.guid()));
                fileData.setUserStatus(Status.fromName(user.getStatus().getName()));
                fileData.setUserStatusReason(user.getStatusReason() != null ? StatusReason.fromName(user.getStatusReason().getName()) : null);
                fileData.setUploadStatus(UploadStatus.CHECKED);
            } else {
                fileData.setDataError(DataError.USER_FOUND_ON_ANOTHER_DOMAIN);
                fileData.setUploadStatus(UploadStatus.FAILED_STAGE_1);
            }
        } catch (UserNotFoundException | UserClientServiceFactoryException userNotFound) {
            fileData.setDataError(DataError.USER_NOT_FOUND);
            fileData.setUploadStatus(UploadStatus.FAILED_STAGE_1);
        } catch (Exception e) {
            log.error("Unable to retrieve user details playerId=" + fileData.getUploadedPlayerId() + " - Gracefully continuing processing of the file" + e.getMessage());
            fileData.setDataError(DataError.UNABLE_TO_RETRIEVE_USER);
            fileData.setUploadStatus(UploadStatus.FAILED_STAGE_1);
        }
    }

    @TimeThisMethod
    public void validate(UserValidation userValidation)
    {
        FileData fileData = FileData.builder()
            .rowNumber(userValidation.getRowNumber())
            .uploadedPlayerId(userValidation.getUploadedPlayerId())
            .amount(userValidation.getAmount())
            .duplicate(userValidation.isDuplicate())
            .build();

        SW.start("doUserChecks");
        doUserChecks(fileData, userValidation.getUploadedDomainName());
        SW.stop();

        SW.start("fetchFileUploadById");
        FileUpload fileUpload = fileUploadService.getFileUploadStatus(userValidation.getFileUploadId());
        List<UploadType> bonusTypes = Arrays.asList(UploadType.BONUS_CASH, UploadType.BONUS_FREESPIN, UploadType.BONUS_INSTANT, UploadType.BONUS_CASINOCHIP);
        SW.stop();

        SW.start("handleBonusValidations");
        if (bonusTypes.stream().anyMatch(t -> t.equals(fileUpload.getUploadType()))) {
            fileUploadService.handleBonusValidation(fileData, fileUpload, UploadStatus.FAILED_STAGE_1);
        }
        SW.stop();

        SW.start("updateFileUploadMeta");
        fileData.setFileUploadMeta(fileUpload);
        fileDataRepository.save(fileData);
        SW.stop();

        SW.start("fetchUploadProgress");
        ProgressResponse progress = fileUploadService.getFileUploadProgress("", "user-verification", fileUpload.getId());
        SW.stop();

        SW.start("postMassActionValidationChecksIfProgressIs100");
        if(progress.getPercentile() == 100) {
            postMassActionValidationChecks(fileUpload);
        }
        SW.stop();

        fileUploadRepository.save(fileUpload);
    }
}
