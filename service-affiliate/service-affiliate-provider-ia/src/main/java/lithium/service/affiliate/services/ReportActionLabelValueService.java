package lithium.service.affiliate.services;

import lithium.service.affiliate.data.entities.LabelValue;
import lithium.service.affiliate.data.entities.ReportAction;
import lithium.service.affiliate.data.entities.ReportActionLabelValue;
import lithium.service.affiliate.data.repositories.ReportActionLabelValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportActionLabelValueService {
	@Autowired LabelValueService labelValueService;
	@Autowired
	ReportActionLabelValueRepository reportActionLabelValueRepository;
	
	public ReportActionLabelValue processCreateRequest(ReportActionLabelValue reportActionLabelValue) {
		return reportActionLabelValueRepository.save(reportActionLabelValue);
	}
	
	public ReportActionLabelValue findOrCreate(ReportAction reportAction, String label, String value) {
		LabelValue lb = labelValueService.findOrCreate(label, value);
		ReportActionLabelValue ralv = reportActionLabelValueRepository.findByReportActionAndLabelValue(reportAction, lb);
		if (ralv != null) return ralv;
		return processCreateRequest(ReportActionLabelValue.builder().reportAction(reportAction).labelValue(lb).build());
	}
}
