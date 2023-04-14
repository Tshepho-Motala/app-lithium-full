package lithium.service.report.games.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.report.games.data.entities.LabelValue;
import lithium.service.report.games.data.entities.ReportAction;
import lithium.service.report.games.data.entities.ReportActionLabelValue;
import lithium.service.report.games.data.repositories.ReportActionLabelValueRepository;

@Service
public class ReportActionLabelValueService {
	@Autowired LabelValueService labelValueService;
	@Autowired ReportActionLabelValueRepository reportActionLabelValueRepository;
	
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