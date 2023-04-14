package lithium.service.user.data.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.LockModeType;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.Status;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends PagingAndSortingRepository<User, Long>, JpaSpecificationExecutor<User> {
	User findByUsername(String username);
	User findByGuid(String guid);
	User findByDomainNameAndUsername(String domain, String username);
	List<User> findByDomainNameInAndUsername(List<String> domainList, String username);
	List<User> findByDomainNameAndCellphoneNumber(String domain, String cellphoneNumber);
	List<User> findByDomainNameInAndCellphoneNumber(List<String> domainList, String cellphoneNumber);
	List<User> findByDomainNameInAndCreatedDateBetween(List<String> domains, Date startDate, Date endDate);
  List<User> findAllByDeletedIsTrueAndStatusIsNot(Status status);

	Iterable<User> findAllByGroups(Group group);
	List<User> findAllByGroupsContains(Group group);
	Page<User> findAllByGroups(Pageable pageRequest, Group group);
	List<User> findByDomainNameAndEmail(String domain, String email);
	List<User> findByDomainNameInAndEmail(List<String> domainList, String email);
  List<User> findByTestAccountTrue(Pageable page);
 	//Used only to update player guids when guid is null
	List<User> findTop100ByGuidIsNull();
	long countByUserCategories_Id(Long id);
	
	@Query(value=
		"select up from lithium.service.user.data.entities.User up "
		+"left outer join up.domain d "
		+"where up.domain.id = d.id "
		+"and d.name = :domainName "
		+"and (up.username like :search "
		+"or up.firstName like :search "
		+"or up.lastName like :search "
		+"or up.email like :search) "
	)
	List<UserProjection> findByDomainNameAndUsernameOrFirstNameOrLastNameOrEmail(@Param("domainName") String domainName, @Param("search") String search);

	@Query(value=
		"select u from lithium.service.user.data.entities.User u "
    +"left outer join u.domain d "
    +"where u.domain.id = d.id "
    +"and d.name in (:domains) "
		+"and (u.username like :search "
		+"or u.firstName like :search "
		+"or u.lastName like :search "
		+"or u.email like :search) "
	)
	List<User> findByDomainNameInAndUsernameOrFirstNameOrLastNameOrEmail(@Param("domains") List<String> domains, @Param("search") String search);

	UserProjection findByDomainNameAndGuid(String domain, String guid); // Added domain as cheat because findByGuid already existing.

    List<User> findByDomainNameAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYear(String domainName, String firstName, String lastName, int dobDay, int dobMonth, int dobYear);
    List<User> findByDomainNameInAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYear(ArrayList<String> domainList, String firstName, String lastName, int dobDay, int dobMonth, int dobYear);
    List<User> findByDomainNameAndFirstNameAndLastNameAndDobDayAndDobMonthAndDobYearAndIdNot(String domainName, String firstName, String lastName, int dobDay, int dobMonth, int dobYear, long notThisUserId);
    List<User> findByDomainNameAndLastNameAndDobDayAndDobMonthAndDobYearAndResidentialAddressPostalCodeAndIdNot(String domainName, String lastName, int dobDay, int dobMonth, int dobYear, String postcode, long notThisUserId);

    @Query("select o from #{#entityName} o where o.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    User findForUpdate(@Param("id") Long id);

  default User findOne(Long id) {
    return findById(id).orElse(null);
  }

}
