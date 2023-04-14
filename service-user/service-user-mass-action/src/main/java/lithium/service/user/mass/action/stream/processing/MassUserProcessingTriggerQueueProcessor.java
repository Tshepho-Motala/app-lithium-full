package lithium.service.user.mass.action.stream.processing;

import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.user.mass.action.api.backoffice.schema.ProgressResponse;
import lithium.service.user.mass.action.data.entities.Action;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.data.repositories.ActionRepository;
import lithium.service.user.mass.action.data.repositories.FileDataRepository;
import lithium.service.user.mass.action.services.FileUploadService;
import lithium.service.user.mass.action.services.MassActionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MassUserProcessingTriggerQueueProcessor {
    @Autowired
    private MassActionService massActionService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    FileDataRepository fileDataRepository;

    @TimeThisMethod
    @StreamListener(MassUserProcessingQueueSink.INPUT)
    public void onProcessingMessage(Long fileDataId) {
        SW.start("retrieve fileData");
        Optional<FileData> fileDataOptional = fileDataRepository.findById(fileDataId);
        if (!fileDataOptional.isPresent()) {
            return;
        }
        FileData fileData = fileDataOptional.get();
        SW.stop();

        SW.start("retrieve fileUploadMeta");
        Optional<FileUpload> result = fileUploadService.retrieveFileUploadById(fileData.getFileUploadMeta().getId());
        SW.stop();

        if (result.isPresent()) {
            FileUpload fileUpload = result.get();

            SW.start("listActions");
            List<Action> actions = actionRepository.findAllByMassActionMeta(fileUpload.getMassActionMeta());
            SW.stop();

            SW.start("performActionsOnFileData");
            massActionService.performActionsOnFileData(fileUpload, fileData, actions);
            SW.stop();

            if (fileUpload.getUploadType().equals(UploadType.PLAYER_INFO) && !fileData.getUploadStatus().equals(UploadStatus.FAILED_STAGE_2)) {
                fileData.setUploadStatus(UploadStatus.DONE);
            }

            fileDataRepository.save(fileData);

            if (fileUpload.getUploadType().equals(UploadType.BONUS_CASH) ||
                fileUpload.getUploadType().equals(UploadType.BONUS_FREESPIN) ||
                fileUpload.getUploadType().equals(UploadType.BONUS_CASINOCHIP) ||
                fileUpload.getUploadType().equals(UploadType.BONUS_INSTANT)) {
                SW.start("getBonusProcessingProgress");
                ProgressResponse progress = fileUploadService.getBonusProcessingProgress(fileUpload);
                SW.stop();

                if (progress.getPercentile() == 100) {
                    fileUploadService.updateFileUploadStatus(UploadStatus.DONE, fileUpload);
                }
            }

            if (fileUpload.getUploadType().equals(UploadType.PLAYER_INFO)) {
                SW.start("getPlayerProcessingProgress");
                ProgressResponse progress = fileUploadService.getPlayerProcessingProgress(fileUpload);
                SW.stop();
                if (progress.getPercentile() == 100) {
                    SW.start("updateFileUploadStatusToDone");
                    fileUploadService.updateFileUploadStatus(UploadStatus.DONE, fileUpload);
                    SW.stop();
                }
            }
        }
    }
}
