package lithium.service.document.data.repositories;

import lithium.service.document.data.entities.DocumentV2;
import lithium.service.document.data.entities.File;
import lithium.service.document.data.entities.Owner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentV2Repository extends PagingAndSortingRepository<DocumentV2, Long> {
	Optional<DocumentV2> findById(long id);
	List<DocumentV2> findAllByOwnerGuidAndSensitiveFalseAndDeletedFalse(String guid);
	List<DocumentV2> findAllByOwnerGuidAndDeletedFalse(String guid);

	@Query(value = "SELECT @@max_allowed_packet", nativeQuery = true)
	Long findPacketSize();
	List<DocumentV2> findAllByDeletedFalse(Pageable page);

	List<DocumentV2> findByOwnerAndDocumentFileFile(Owner owner, File file);
}
