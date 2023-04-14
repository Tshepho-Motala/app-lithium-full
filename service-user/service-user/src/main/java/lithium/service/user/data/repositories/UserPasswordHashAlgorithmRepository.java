package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordHashAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface UserPasswordHashAlgorithmRepository extends JpaRepository<UserPasswordHashAlgorithm, Long> {
  Optional<UserPasswordHashAlgorithm> findByUserId(Long userId);

  @Transactional(propagation = Propagation.REQUIRED)
  @Modifying
  void deleteByUser(User user);
}
