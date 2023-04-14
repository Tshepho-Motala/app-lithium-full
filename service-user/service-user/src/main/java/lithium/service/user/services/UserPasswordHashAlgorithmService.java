package lithium.service.user.services;

import java.util.Optional;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordHashAlgorithm;
import lithium.service.user.data.repositories.UserPasswordHashAlgorithmRepository;
import lithium.service.user.enums.PasswordHashAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserPasswordHashAlgorithmService {
  private final UserPasswordHashAlgorithmRepository repository;

  @Autowired
  public UserPasswordHashAlgorithmService(UserPasswordHashAlgorithmRepository repository) {
    this.repository = repository;
  }

  public Optional<UserPasswordHashAlgorithm> getUserPasswordHashAlgorithm(Long userId) {
    return repository.findByUserId(userId);
  }

  public UserPasswordHashAlgorithm save(User user, String salt, PasswordHashAlgorithm hashAlgorithm) {
    return repository.save(
        UserPasswordHashAlgorithm.builder()
            .user(user)
            .salt(salt)
            .hashAlgorithm(hashAlgorithm)
            .build()
    );
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public void delete(User user) {
    repository.deleteByUser(user);
  }
}
