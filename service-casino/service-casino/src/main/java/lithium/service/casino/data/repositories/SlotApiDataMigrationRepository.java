package lithium.service.casino.data.repositories;

import lithium.service.casino.data.entities.SlotApiDataMigration;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SlotApiDataMigrationRepository extends PagingAndSortingRepository<SlotApiDataMigration, Long> {

    default SlotApiDataMigration findOne(Long id) {
        return findById(id).orElse(null);
    }

}
