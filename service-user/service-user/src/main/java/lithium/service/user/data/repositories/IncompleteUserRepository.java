package lithium.service.user.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import lithium.service.user.data.entities.IncompleteUser;
import java.util.Optional;

public interface IncompleteUserRepository extends JpaRepository<IncompleteUser, Long>, PagingAndSortingRepository<IncompleteUser, Long> ,JpaSpecificationExecutor<IncompleteUser>  {
	IncompleteUser findByFirstName(String firstname);
	IncompleteUser findByEmail(String email);
  default IncompleteUser findOne(Long id) {
    return Optional.ofNullable(id)
        .flatMap(this::findById)
        .orElse(null);
  }

}
