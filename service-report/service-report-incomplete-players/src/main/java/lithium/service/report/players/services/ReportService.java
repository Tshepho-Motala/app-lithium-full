package lithium.service.report.players.services;

import lithium.service.report.players.data.entities.ReportAction;
import lithium.service.report.players.data.entities.ReportActionLabelValue;
import lithium.service.report.players.data.entities.ReportFilter;
import lithium.service.report.players.data.entities.ReportRevision;
import lithium.service.report.players.data.objects.ReportActionBasic;
import lithium.service.report.players.data.objects.ReportFilterBasic;
import lithium.service.report.players.data.repositories.ReportActionLabelValueRepository;
import lithium.service.report.players.data.repositories.ReportActionRepository;
import lithium.service.report.players.data.repositories.ReportFilterRepository;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {
	@Autowired TokenStore tokenStore;
	@Autowired ReportFilterRepository reportFilterRepository;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired ReportActionLabelValueRepository reportActionLabelValueRepository;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;
	
	public void copy(ReportRevision from, ReportRevision to, String updateBy) {
		to.setReport(from.getReport());
		to.setName(from.getName());
		to.setDescription(from.getDescription());
		to.setUpdateDate(new Date());
		to.setUpdateBy(updateBy);
		to.setNotifyEmail(from.getNotifyEmail());
		to.setAllFiltersApplicable(from.getAllFiltersApplicable());
		to.setCron(from.getCron());
		to.setChosenDateString(from.getChosenDateString());
		to.setChosenTimeString(from.getChosenTimeString());
	}
	
	public void copyFilters(ReportRevision from, ReportRevision to) {
		List<ReportFilter> filters = reportFilterRepository.findByReportRevision(from);
		filters.forEach(filter -> {
			reportFilterRepository.save(
				ReportFilter.builder()
				.reportRevision(to)
				.field(filter.getField())
				.operator(filter.getOperator())
				.value(filter.getValue())
				.build()
			);
		});
	}
	
	public void copyActionsAndActionLabelValues(ReportRevision from, ReportRevision to) {
		List<ReportAction> actions = reportActionRepository.findByReportRevision(from);
		actions.forEach(action -> {
			ReportAction editAction = reportActionRepository.save(
				ReportAction.builder()
				.reportRevision(to)
				.actionType(action.getActionType())
				.build()
			);
			action.getLabelValueList().forEach(lv -> {
				reportActionLabelValueService.findOrCreate(
					editAction, lv.getLabelValue().getLabel().getName(), lv.getLabelValue().getValue());
			});
		});
	}
	
	public void deleteFilters(List<ReportFilter> filters) {
		for (ReportFilter filter: filters) {
			reportFilterRepository.delete(filter);
		}
	}
	
	public void deleteActionsAndActionLabelValues(List<ReportAction> actions) {
		for (ReportAction action: actions) {
			for (ReportActionLabelValue ralv: action.getLabelValueList()) {
				reportActionLabelValueRepository.delete(ralv);
			}
			reportActionRepository.delete(action);
		}
	}
	
	public void addFilters(ReportRevision rev, ReportFilterBasic[] filters) {
		for (int i = 0; i < filters.length; i++) {
			ReportFilter reportFilter = ReportFilter.builder()
				.reportRevision(rev)
				.field(filters[i].getField())
				.operator(filters[i].getOperator())
				.value(filters[i].getValue())
				.build();
			reportFilter = reportFilterRepository.save(reportFilter);
		}
	}
	
	public void addActionsAndActionLabelValues(ReportRevision rev, ReportActionBasic[] actions) {
		for (int i = 0; i < actions.length; i++) {
			ReportAction reportAction = ReportAction.builder()
				.reportRevision(rev)
				.actionType(actions[i].getActionType())
				.build();
			reportAction = reportActionRepository.save(reportAction);
			if (actions[i].getRecipients() != null && actions[i].getRecipients().length > 0) {
				String label = "";
				if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL;
				} else if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_STATS_RECIPIENT_EMAIL;
				}
				if (label != null && !label.isEmpty()) {
					for (String recipient: actions[i].getRecipients()) {
						reportActionLabelValueService.findOrCreate(reportAction, label, recipient);
					}
				}
			}
			if (actions[i].getEmailTemplate() != null &&
					!actions[i].getEmailTemplate().isEmpty()) {
				String label = "";
				if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE;
				} else if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL)) {
					label = ReportActionService.LABEL_REPORT_STATS_EMAIL_TEMPLATE;
				} else if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_EMAIL_TO_PLAYER)) {
					label = ReportActionService.LABEL_REPORT_PLAYER_EMAIL_TEMPLATE;
				}
				if (label != null && !label.isEmpty()) {
					reportActionLabelValueService.findOrCreate(reportAction, label, actions[i].getEmailTemplate());
				}
			}
			if (actions[i].getSmsTemplate() != null &&
					!actions[i].getSmsTemplate().isEmpty()) {
				String label = "";
				if (reportAction.getActionType().equalsIgnoreCase(
						ReportActionService.REPORT_ACTION_SEND_SMS_TO_PLAYER)) {
					label = ReportActionService.LABEL_REPORT_PLAYER_SMS_TEMPLATE;
				}
				if (label != null && !label.isEmpty()) {
					reportActionLabelValueService.findOrCreate(reportAction, label, actions[i].getSmsTemplate());
				}
			}
		}
	}
	
	public void checkPermission(String domainName, Principal principal) {
		LithiumTokenUtil tokenUtil = LithiumTokenUtil.builder(tokenStore, principal).build();
		if (!tokenUtil.hasRole(domainName, "REPORT_INCOMPLETE_PLAYERS"))
			throw new AccessDeniedException("User does not have access to reports for this domain");
	}
}