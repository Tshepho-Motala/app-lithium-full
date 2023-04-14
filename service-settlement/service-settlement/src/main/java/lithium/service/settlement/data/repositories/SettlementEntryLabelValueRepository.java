package lithium.service.settlement.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.settlement.data.entities.LabelValue;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.data.entities.SettlementEntryLabelValue;

public interface SettlementEntryLabelValueRepository extends PagingAndSortingRepository<SettlementEntryLabelValue, Long>, JpaSpecificationExecutor<SettlementEntryLabelValue> {
	SettlementEntryLabelValue findBySettlementEntryAndLabelValue(SettlementEntry settlementEntry, LabelValue lv);
}
