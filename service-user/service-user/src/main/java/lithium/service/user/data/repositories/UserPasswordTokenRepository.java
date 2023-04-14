package lithium.service.user.data.repositories;

import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserPasswordToken;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface UserPasswordTokenRepository extends PagingAndSortingRepository<UserPasswordToken, Long>, JpaSpecificationExecutor<UserPasswordToken> {
	List<UserPasswordToken> findByUser(User user);
	UserPasswordToken findByUserAndToken(User user, String token);

	@Transactional
	@Modifying
	void deleteByUser(User user);
}