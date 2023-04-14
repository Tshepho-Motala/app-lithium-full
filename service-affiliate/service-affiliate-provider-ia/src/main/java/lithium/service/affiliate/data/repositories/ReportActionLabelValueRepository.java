package lithium.service.affiliate.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.affiliate.data.entities.Label;
import lithium.service.affiliate.data.entities.LabelValue;
import lithium.service.affiliate.data.entities.ReportAction;
import lithium.service.affiliate.data.entities.ReportActionLabelValue;
import lithium.service.affiliate.data.entities.ReportRevision;

public interface ReportActionLabelValueRepository extends PagingAndSortingRepository<ReportActionLabelValue, Long> {
	ReportActionLabelValue findByReportActionAndLabelValue(ReportAction reportAction, LabelValue labelValue);
	List<ReportActionLabelValue> findByReportActionReportRevisionAndLabelValueLabel(ReportRevision reportRevision, Label label);
	List<ReportActionLabelValue> deleteByReportAction(ReportAction reportAction);
}
