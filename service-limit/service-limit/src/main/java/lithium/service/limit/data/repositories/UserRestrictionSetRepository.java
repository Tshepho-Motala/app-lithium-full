package lithium.service.limit.data.repositories;

import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.entities.UserRestrictionSet;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface UserRestrictionSetRepository extends PagingAndSortingRepository<UserRestrictionSet, Long>, JpaSpecificationExecutor<UserRestrictionSet> {
	UserRestrictionSet findByUserAndSet(User user, DomainRestrictionSet set);
	List<UserRestrictionSet> findByUserGuid(String userGuid);
	UserRestrictionSet findByUserGuidAndSet(String userGuid, DomainRestrictionSet set);
	List<UserRestrictionSet> findAllBySetDeletedFalse(Pageable page);
	Page<UserRestrictionSet> findAllBySetAndAndActiveToIsNotNullAndActiveToBefore(DomainRestrictionSet set, Date activeTo, Pageable pageable);
	Long countBySet(DomainRestrictionSet set);
	default UserRestrictionSet findOne(Long id) {
		return findById(id).orElse(null);
	}
}
