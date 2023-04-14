package lithium.service.mail.data.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.User;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByGuid(String guid);

    List<User> findUsersByFirstNameNullOrLastNameNull(Pageable page);

    long countByFirstNameNullOrLastNameNull();

    @Transactional
    @Modifying
    @Query("update User e set e.firstName = :firstname, e.lastName = :lastname where e.id = :id")
    void updateFullNameById(@Param("id") long id, @Param("firstname") String firstName, @Param("lastname") String lastname);
}
