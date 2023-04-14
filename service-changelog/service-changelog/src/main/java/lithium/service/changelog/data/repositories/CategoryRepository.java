package lithium.service.changelog.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.changelog.data.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends FindOrCreateByNameRepository<Category, Long>, JpaRepository<Category, Long> {
}
