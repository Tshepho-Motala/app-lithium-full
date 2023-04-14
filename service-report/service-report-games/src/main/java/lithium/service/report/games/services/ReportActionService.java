package lithium.service.report.games.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.report.games.data.entities.ReportRevision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.report.games.data.entities.Label;
import lithium.service.report.games.data.entities.ReportAction;
import lithium.service.report.games.data.entities.ReportActionLabelValue;
import lithium.service.report.games.data.entities.ReportRun;
import lithium.service.report.games.data.repositories.ReportActionLabelValueRepository;
import lithium.service.report.games.data.repositories.ReportActionRepository;
import lithium.service.report.games.data.repositories.ReportRunResultsRepository;
import lombok.extern.slf4j.Slf4j;

import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_COMPLETED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PROCESSED_RECORDS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_BY;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_TOTAL_RECORDS;

@Service
@Slf4j
public class ReportActionService {
	@Autowired LithiumServiceClientFactory lithiumServiceClientFactory;
	@Autowired ReportRunResultsRepository reportRunResultsRepository;
	@Autowired ReportRunResultExcelService reportRunResultExcelService;
	@Autowired ReportActionRepository reportActionRepository;
	@Autowired LabelService labelService;
	@Autowired ReportActionLabelValueRepository reportActionLabelValueRepository;
	@Autowired MailStream mailStream;
	
	public static final String REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL = "sendFullReportViaEmail";
	public static final String REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL = "sendReportStatsViaEmail";
	
	public static final String LABEL_REPORT_FULL_RECIPIENT_EMAIL = "reportFullRecipientEmail";
	public static final String LABEL_REPORT_STATS_RECIPIENT_EMAIL = "reportStatsRecipientEmail";
	public static final String LABEL_REPORT_FULL_EMAIL_TEMPLATE = "reportFullEmailTemplate";
	public static final String LABEL_REPORT_STATS_EMAIL_TEMPLATE = "reportStatsEmailTemplate";
	
	public void processActions(ReportRun run) throws Exception {
		List<ReportAction> actions = reportActionRepository.findByReportRevision(run.getReportRevision());
		if (actions.size() > 0) {
			for (ReportAction action: actions) {
				switch (action.getActionType()) {
					case ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL:
						sendReportViaEmail(run);
						break;
					case ReportActionService.REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL:
						sendReportStatsViaEmail(run);
						break;
				} 
			}
		}
	}
	
	private Domain getExternalDomain(String domainName) throws LithiumServiceClientFactoryException {
		DomainClient domainClient = lithiumServiceClientFactory.target(DomainClient.class, "service-domain", true);
		Response<Domain> response = domainClient.findByName(domainName);
		if (response.isSuccessful()) {
			return response.getData();
		}
		return null;
	}
	
	private void sendReportViaEmail(ReportRun run) throws Exception {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		reportRunResultExcelService.xls(run, byteArrayOutputStream);
		byte[] bytes = byteArrayOutputStream.toByteArray();
		Label lblEmailTemplate = labelService.findOrCreate(LABEL_REPORT_FULL_EMAIL_TEMPLATE);
		List<ReportActionLabelValue> emailTemplateList = 
				reportActionLabelValueRepository.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), lblEmailTemplate);
		String emailTemplate = (emailTemplateList.size() > 0)? emailTemplateList.get(0).getLabelValue().getValue(): "";
		if (emailTemplate != null && !emailTemplate.isEmpty()) {
			Label label = labelService.findOrCreate(LABEL_REPORT_FULL_RECIPIENT_EMAIL);
			List<ReportActionLabelValue> rrlvs = reportActionLabelValueRepository
					.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), label);
			for (ReportActionLabelValue rrlv: rrlvs) {
				sendEmail(
					run.getReport().getDomainName(),
					rrlv.getLabelValue().getValue(),
					null,
					emailTemplate,
					"en",
					reportEmailPlaceholders(run, getExternalDomain(run.getReport().getDomainName())),
					run.getReportRevision().getName()+".xlsx",
					bytes
				);
			}
		} else {
			log.warn("Email template name for action sendReportViaEmail not found on report revision " 
					+ run.getReportRevision().getId() + " :: " + run.getReportRevision().getName());
		}
	}
	
	private void sendReportStatsViaEmail(ReportRun run) throws IOException, LithiumServiceClientFactoryException {
		Label lblEmailTemplate = labelService.findOrCreate(LABEL_REPORT_STATS_EMAIL_TEMPLATE);
		List<ReportActionLabelValue> emailTemplateList =
				reportActionLabelValueRepository.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), lblEmailTemplate);
		String emailTemplate = (emailTemplateList.size() > 0)? emailTemplateList.get(0).getLabelValue().getValue(): "";
		if (emailTemplate != null && !emailTemplate.isEmpty()) {
			Label label = labelService.findOrCreate(LABEL_REPORT_STATS_RECIPIENT_EMAIL);
			List<ReportActionLabelValue> rrlvs = reportActionLabelValueRepository
					.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), label);
			for (ReportActionLabelValue rrlv: rrlvs) {
				sendEmail(
					run.getReport().getDomainName(),
					rrlv.getLabelValue().getValue(),
					null,
					emailTemplate,
					"en",
					reportEmailPlaceholders(run, getExternalDomain(run.getReport().getDomainName())),
					null,
					null
				);
			}
		} else {
			log.warn("Email template name for action sendReportStatsViaEmail not found on report revision " 
						+ run.getReportRevision().getId() + " :: " + run.getReportRevision().getName());
		}
	}
	
	private void sendEmail(String domainName, String emailAddress, String userGuid, String emailTemplateName, String emailTemplateLang,
						   Set<Placeholder> placeholders, String attachmentName, byte[] attachmentData) throws IOException {
		mailStream.process(EmailData.builder()
				.authorSystem()
				.emailTemplateName(emailTemplateName)
				.emailTemplateLang(emailTemplateLang)
				.to(emailAddress)
				.userGuid(userGuid)
				.priority(1)
				.attachmentName(attachmentName)
				.attachmentData(attachmentData)
				.placeholders(placeholders)
				.domainName(domainName)
				.build()
		);
	}
	
	private Set<Placeholder> reportEmailPlaceholders(ReportRun reportRun, Domain domain) {
		Set<Placeholder> placeholders = new HashSet<>();
		placeholders.add(REPORT_NAME.from(ofNullable(reportRun.getReportRevision()).map(ReportRevision::getName)));
		placeholders.add(REPORT_STARTED_ON.from(reportRun.getStartedOn()));
		placeholders.add(REPORT_COMPLETED_ON.from(reportRun.getCompletedOn()));
		placeholders.add(REPORT_STARTED_BY.from(reportRun.getStartedBy()));
		placeholders.add(REPORT_TOTAL_RECORDS.from(reportRun.getTotalRecords()));
		placeholders.add(REPORT_PROCESSED_RECORDS.from(reportRun.getProcessedRecords()));
		if (domain != null) {
			placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
		}
		return placeholders;
	}
}