package lithium.service.report.players.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.players.data.entities.Label;
import lithium.service.report.players.data.entities.LabelValue;
import lithium.service.report.players.data.entities.ReportAction;
import lithium.service.report.players.data.entities.ReportActionLabelValue;
import lithium.service.report.players.data.entities.ReportRevision;

public interface ReportActionLabelValueRepository extends PagingAndSortingRepository<ReportActionLabelValue, Long> {
	ReportActionLabelValue findByReportActionAndLabelValue(ReportAction reportAction, LabelValue labelValue);
	List<ReportActionLabelValue> findByReportActionReportRevisionAndLabelValueLabel(ReportRevision reportRevision, Label label);
	List<ReportActionLabelValue> deleteByReportAction(ReportAction reportAction);
}