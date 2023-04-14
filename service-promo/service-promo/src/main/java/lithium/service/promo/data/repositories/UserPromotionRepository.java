package lithium.service.promo.data.repositories;

import java.util.List;

import javax.persistence.LockModeType;
import lithium.service.promo.data.entities.Period;
import lithium.service.promo.data.entities.Promotion;
import lithium.service.promo.data.entities.UserPromotion;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.promo.data.entities.User;
import org.springframework.data.repository.query.Param;

public interface UserPromotionRepository extends PagingAndSortingRepository<UserPromotion, Long>, JpaSpecificationExecutor<UserPromotion> {
	List<UserPromotion> findByUserGuidAndPromotionRevisionPromotionAndPeriod(String guid, Promotion promotion, Period period);
	List<UserPromotion> findByUserGuidAndPromotionRevisionPromotion(String guid, Promotion promotion);
	UserPromotion findByUserAndCompletedIsNullAndPromotionCompleteIsFalseAndExpiredIsFalseAndActiveIsTrue(User user);

	@Query("select o from #{#entityName} o LEFT OUTER JOIN o.user u LEFT OUTER JOIN o.promotionRevision mr LEFT OUTER JOIN o.period p where u.guid = :guid and mr.id = :promotionRevisionId and p.id = :periodId")
	@Lock( LockModeType.PESSIMISTIC_FORCE_INCREMENT)
	List<UserPromotion> findByUserGuidAndPromotionRevisionAndPeriodForUpdate(@Param("guid") String guid, @Param("promotionRevisionId") Long promotionRevisionId, @Param("periodId") Long periodId);

	int countByUserGuidAndPromotionRevisionId(@Param("guid") String guid, @Param("promotionRevisionId") Long promotionRevisionId);
	int countByUserGuidAndPromotionRevisionIdAndCompletedIsNotNullAndPromotionCompleteIsTrue(@Param("guid") String guid, @Param("promotionRevisionId") Long promotionRevisionId);

	default UserPromotion findOne(Long id) {
		return findById(id).orElse(null);
	}
}
