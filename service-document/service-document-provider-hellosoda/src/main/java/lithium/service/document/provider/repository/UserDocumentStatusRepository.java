package lithium.service.document.provider.repository;

import lithium.service.document.provider.entity.UserDocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserDocumentStatusRepository extends JpaRepository<UserDocumentStatus, String> {
    List<UserDocumentStatus> findAllByUserId(Long userId);
}