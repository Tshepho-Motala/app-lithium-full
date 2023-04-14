package lithium.service.changelog.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.changelog.data.entities.SubCategory;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubCategoryRepository extends FindOrCreateByNameRepository<SubCategory, Long> {
    Iterable<SubCategory> findByCategoryName(String category);
    List<SubCategory> findSubCategoriesByCategoryIsNull();
    SubCategory findByName(String subCategoryName);
}
