package lithium.service.user.data.repositories;

import java.util.List;
import lithium.service.user.data.entities.EmailValidationToken;
import lithium.service.user.data.entities.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmailValidationTokenRepository extends PagingAndSortingRepository<EmailValidationToken, Long> {
  List<EmailValidationToken> findByUser(User user);
  EmailValidationToken findByUserAndEmail(User user, String email);
	EmailValidationToken findByUserDomainNameAndToken(String domainName, String token);
}
