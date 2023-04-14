package lithium.service.changelog.data.repositories;

import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChangeLogRepository extends PagingAndSortingRepository<ChangeLog, Long>, JpaSpecificationExecutor<ChangeLog> {

	Page<ChangeLog> findByEntityAndEntityRecordId(ChangeLogEntity entity, long entityRecordId, Pageable page);
	Page<ChangeLog> findByAuthorUser(User authorUser, Pageable page);
	ChangeLog findByIdAndAuthorUserId(long logId, long userId);
	ChangeLog findByIdAndAuthorUserGuid(long recordId, String guid);
	default ChangeLog findOne(Long id) {
		return findById(id).orElse(null);
	}

    List<ChangeLog> findByCategoryNameAndSubCategoryName(String categoryName, String subcategoryName, Pageable page);
}
