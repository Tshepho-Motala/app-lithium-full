package lithium.service.report.games.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.report.games.data.repositories.ReportActionRepository;
import lithium.service.report.games.data.repositories.ReportFilterRepository;
import lithium.service.report.games.data.repositories.ReportRepository;
import lithium.service.report.games.data.repositories.ReportRevisionRepository;
import lithium.service.report.games.data.repositories.ReportRunRepository;

@Service
public class TestDataService {
	
	@Autowired ReportRepository reportRepository;
	@Autowired ReportRunRepository reportRunRepository;
	@Autowired ReportRevisionRepository reportRevisionRepository;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	
	public void load() {
	}
	
}