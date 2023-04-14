package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.client.enums.Status;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileData, Long> {

    Page<FileData> findAllByFileUploadMeta(FileUpload fileUpload, Pageable pageable);

    List<FileData> findAllByFileUploadMetaAndUploadStatusInAndUserStatus(FileUpload fileUpload, List<UploadStatus> uploadStatus, Status userStatus);
    List<FileData> findAllByFileUploadMetaAndDuplicateFalseAndUploadStatus(FileUpload fileUpload, UploadStatus uploadStatus);

    Optional<FileData> findFirstByFileUploadMetaAndPlayer(FileUpload fileUpload, User player);
    Optional<FileData> findByFileUploadMetaAndRowNumber(FileUpload fileUpload, Long rowNumber);

    Long countFileDataByFileUploadMeta(FileUpload fileUpload);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatus(FileUpload fileUpload, Collection<UploadStatus> uploadStatus, Status userStatus);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusInAndUserStatusNotNull(FileUpload fileUpload, Collection<UploadStatus> uploadStatus);
    Long countFileDataByFileUploadMetaAndUploadStatusInAndUserStatus(FileUpload fileUpload, Collection<UploadStatus> uploadStatus, Status userStatus);
    Long countFileDataByFileUploadMetaAndDuplicateTrueAndUserStatus(FileUpload fileUpload, Status userStatus);
    Long countFileDataByFileUploadMetaAndDuplicateTrueAndUserStatusNotNull(FileUpload fileUpload);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatusAndUploadStatusNot(FileUpload fileUpload, Status userStatus, UploadStatus uploadStatus);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUploadStatusNotAndUserStatusNotNull(FileUpload fileUpload, UploadStatus uploadStatus);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatus(FileUpload fileUpload, Status userStatus);
    Long countFileDataByFileUploadMetaAndDuplicateFalseAndUserStatusNotNull(FileUpload fileUpload);
    Long countFileDataByFileUploadMetaAndUserStatusNot(FileUpload fileUpload, Status userStatus);
    Long countFileDataByFileUploadMetaAndUserStatusIsNull(FileUpload fileUpload);
}