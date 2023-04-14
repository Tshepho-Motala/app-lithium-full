package lithium.service.promo.data.repositories;

import java.util.List;

import lithium.service.promo.data.entities.PromotionStatSummary;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.promo.data.entities.PromotionStat;
import lithium.service.promo.data.entities.Period;

public interface PromotionStatSummaryRepository extends PagingAndSortingRepository<PromotionStatSummary, Long> {
	PromotionStatSummary findByPeriodAndPromotionStat(Period period, PromotionStat stat);
	List<PromotionStatSummary> findByPromotionStatNameAndOwnerGuid(String name, String playerGuid);
	PromotionStatSummary findByPeriodAndPromotionStatNameAndOwnerGuid(Period period, String name, String playerGuid);
//	PromotionStatSummary findByMissionStatNameAndOwnerGuidAndMissionStatLabelValueLabelValueLabelNameIn(String name, String ownerGuid, String[] label);
}