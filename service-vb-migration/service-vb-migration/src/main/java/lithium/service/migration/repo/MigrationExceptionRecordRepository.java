package lithium.service.migration.repo;

import lithium.service.libraryvbmigration.data.entities.MigrationExceptionRecord;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface MigrationExceptionRecordRepository extends PagingAndSortingRepository<MigrationExceptionRecord, Long> {

}
