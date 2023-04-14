package lithium.service.changelog.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogFieldChange;

public interface ChangeLogFieldChangeRepository extends PagingAndSortingRepository<ChangeLogFieldChange, Long> {
	
	List<ChangeLogFieldChange> findByChangeLog(ChangeLog changeLog);
	List<ChangeLogFieldChange> findByChangeLogIn(List<ChangeLog> changeLogs);
}
