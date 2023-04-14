package lithium.service.user.mass.action.data.repositories;

import lithium.service.user.mass.action.data.entities.FileMeta;
import lithium.service.user.mass.action.data.entities.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetaRepository extends JpaRepository<FileMeta, Long> {

    FileMeta findByFileUploadMeta(FileUpload fileUpload);
}