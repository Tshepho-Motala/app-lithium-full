package lithium.service.settlement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.settlement.data.entities.LabelValue;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.data.entities.SettlementEntryLabelValue;
import lithium.service.settlement.data.repositories.SettlementEntryLabelValueRepository;

@Service
public class SettlementEntryLabelValueService {
	@Autowired LabelValueService labelValueService;
	@Autowired SettlementEntryLabelValueRepository settlementEntryLabelValueRepository;
	
	public SettlementEntryLabelValue processCreateRequest(SettlementEntryLabelValue settlementEntryLabelValue) {
		return settlementEntryLabelValueRepository.save(settlementEntryLabelValue);
	}
	
	public SettlementEntryLabelValue findOrCreate(SettlementEntry settlementEntry, String label, String value) {
		LabelValue lb = labelValueService.findOrCreate(label, value);
		SettlementEntryLabelValue selv = settlementEntryLabelValueRepository.findBySettlementEntryAndLabelValue(settlementEntry, lb);
		if (selv != null) return selv;
		return processCreateRequest(SettlementEntryLabelValue.builder().settlementEntry(settlementEntry).labelValue(lb).build());
	}
}