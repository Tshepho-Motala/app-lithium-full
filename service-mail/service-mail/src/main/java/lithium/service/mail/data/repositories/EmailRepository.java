package lithium.service.mail.data.repositories;

import lithium.service.mail.data.entities.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;

public interface EmailRepository extends PagingAndSortingRepository<Email, Long>, JpaSpecificationExecutor<Email> {
    @Transactional
    @Modifying
    @Query("UPDATE #{#entityName} o " +
            "SET o.processing = false " +
            "WHERE o.failed = false " +
            "AND o.processing = true " +
            "AND o.processingStarted < :threshold")
    void updateProcessingToFalseOnStuckMail(@Param("threshold") Date threshold);

	Page<Email> findByFailedFalseAndProcessingFalseAndSentDateIsNullAndErrorCountLessThanOrderByPriorityAscCreatedDateAsc(
			int errorThreshold, Pageable pageRequest);
	default Email findOne(Long id) {
		return findById(id).orElse(null);
	}
}