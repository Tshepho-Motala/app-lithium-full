package lithium.service.user.search.data.repositories.user;

import lithium.service.user.data.entities.UserCategory;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository("user.UserCategoryRepository")
public interface UserCategoryRepository extends PagingAndSortingRepository<UserCategory, Long>, JpaSpecificationExecutor<UserCategory> {

  default UserCategory findOne(Long id) {
    return findById(id).orElse(null);
  }
}
