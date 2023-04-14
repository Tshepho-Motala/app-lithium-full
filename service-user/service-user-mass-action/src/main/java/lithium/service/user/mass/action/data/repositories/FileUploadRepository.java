package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.List;
import java.util.Optional;

public interface FileUploadRepository extends PagingAndSortingRepository<FileUpload, Long> {

    List<FileUpload> findFileUploadByUploadStatusAndUploadType(UploadStatus uploadStatus, UploadType uploadType);
    Optional<FileUpload> findFileUploadById(Long id);
    Page<FileUpload> findFileUploadByAuthorAndDomainNameAndUploadType(User author, String domainName, UploadType uploadType, Pageable pageable);
    Page<FileUpload> findFileUploadByDomainNameAndUploadType(String domainName, UploadType uploadType, Pageable pageable);
}
