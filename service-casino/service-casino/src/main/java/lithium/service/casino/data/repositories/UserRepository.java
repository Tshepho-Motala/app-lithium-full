package lithium.service.casino.data.repositories;


import lithium.jpa.repository.FindOrCreateByGuidRepository;
import lithium.service.casino.data.entities.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends FindOrCreateByGuidRepository<User, Long> {
	User findByGuid(String guid);
}
