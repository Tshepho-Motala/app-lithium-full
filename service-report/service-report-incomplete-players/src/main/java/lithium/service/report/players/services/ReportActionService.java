package lithium.service.report.players.services;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.report.players.data.entities.Label;
import lithium.service.report.players.data.entities.ReportAction;
import lithium.service.report.players.data.entities.ReportActionLabelValue;
import lithium.service.report.players.data.entities.ReportRevision;
import lithium.service.report.players.data.entities.ReportRun;
import lithium.service.report.players.data.entities.ReportRunResults;
import lithium.service.report.players.data.entities.StringValue;
import lithium.service.report.players.data.repositories.ReportActionLabelValueRepository;
import lithium.service.report.players.data.repositories.ReportActionRepository;
import lithium.service.report.players.data.repositories.ReportRunRepository;
import lithium.service.report.players.data.repositories.ReportRunResultsRepository;
import lithium.service.sms.client.objects.SMSBasic;
import lithium.service.sms.client.stream.SMSStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_COMPLETED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PROCESSED_RECORDS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_BY;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_TOTAL_RECORDS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_CREATE_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_EMAIL_ADDRESS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_FIRST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_LAST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_NAME;

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
	@Autowired SMSStream smsStream;
	@Autowired ReportRunRepository reportRunRepository;
	
	public static final String REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL = "sendFullReportViaEmail";
	public static final String REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL = "sendReportStatsViaEmail";
	public static final String REPORT_ACTION_SEND_EMAIL_TO_PLAYER = "sendEmailToPlayer";
	public static final String REPORT_ACTION_SEND_SMS_TO_PLAYER = "sendSMSToPlayer";
	
	public static final String LABEL_REPORT_FULL_RECIPIENT_EMAIL = "reportFullRecipientEmail";
	public static final String LABEL_REPORT_STATS_RECIPIENT_EMAIL = "reportStatsRecipientEmail";
	public static final String LABEL_REPORT_FULL_EMAIL_TEMPLATE = "reportFullEmailTemplate";
	public static final String LABEL_REPORT_STATS_EMAIL_TEMPLATE = "reportStatsEmailTemplate";
	public static final String LABEL_REPORT_PLAYER_EMAIL_TEMPLATE = "reportPlayerEmailTemplate";
	public static final String LABEL_REPORT_PLAYER_SMS_TEMPLATE = "reportPlayerSMSTemplate";
	
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
					case ReportActionService.REPORT_ACTION_SEND_EMAIL_TO_PLAYER:
						sendEmailToPlayer(run);
					case ReportActionService.REPORT_ACTION_SEND_SMS_TO_PLAYER:
						sendSMSToPlayer(run);
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
				run.setActionsPerformed((run.getActionsPerformed() != null)? run.getActionsPerformed() + 1: 1);
				run = reportRunRepository.save(run);
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
				run.setActionsPerformed((run.getActionsPerformed() != null)? run.getActionsPerformed() + 1: 1);
				run = reportRunRepository.save(run);
			}
		} else {
			log.warn("Email template name for action sendReportStatsViaEmail not found on report revision " 
						+ run.getReportRevision().getId() + " :: " + run.getReportRevision().getName());
		}
	}
	
	public void sendSMSToPlayer(ReportRun run) throws UnsupportedEncodingException, LithiumServiceClientFactoryException {
		Label lblSmsTemplate = labelService.findOrCreate(LABEL_REPORT_PLAYER_SMS_TEMPLATE);
		List<ReportActionLabelValue> smsTemplateList = reportActionLabelValueRepository
				.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), lblSmsTemplate);
		String smsTemplate = (smsTemplateList.size() > 0)? smsTemplateList.get(0).getLabelValue().getValue(): "";
		if (smsTemplate != null && !smsTemplate.isEmpty()) {
			boolean doProcess = true;
			int page = 0;
			while (doProcess) {
				PageRequest pr = PageRequest.of(page, 10);
				Page<ReportRunResults> results = reportRunResultsRepository.findByReportRunId(run.getId(), pr);
				for (ReportRunResults result: results) {
					if (result.getCellphoneNumber() != null && !result.getCellphoneNumber().getValue().isEmpty()) {
						sendSMS(
							run.getReport().getDomainName(),
							result.getCellphoneNumber().getValue(),
							result.getUsername() != null ? run.getReport().getDomainName() + "/" + result.getUsername().getValue() : null,
							smsTemplate,
							"en",
							playerPlaceholders(result, getExternalDomain(run.getReport().getDomainName()))
						);
						run.setActionsPerformed((run.getActionsPerformed() != null) ? run.getActionsPerformed() + 1 : 1);
						run = reportRunRepository.save(run);
					}
				}
				page++;
				if (!results.hasNext()) doProcess = false;
			}
		}
	}
	
	private void sendEmailToPlayer(ReportRun run) throws IOException, LithiumServiceClientFactoryException {
		Label lblEmailTemplate = labelService.findOrCreate(LABEL_REPORT_PLAYER_EMAIL_TEMPLATE);
		List<ReportActionLabelValue> emailTemplateList = reportActionLabelValueRepository
				.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), lblEmailTemplate);
		String emailTemplate = (emailTemplateList.size() > 0)? emailTemplateList.get(0).getLabelValue().getValue(): "";
		if (emailTemplate != null && !emailTemplate.isEmpty()) {
			boolean doProcess = true;
			int page = 0;
			while (doProcess) {
				PageRequest pr = PageRequest.of(page, 10);
				Page<ReportRunResults> results = reportRunResultsRepository.findByReportRunId(run.getId(), pr);
				for (ReportRunResults result: results) {
					if (result.getEmail() != null && !result.getEmail().getValue().isEmpty()) {
						sendEmail(
							run.getReport().getDomainName(),
							result.getEmail().getValue(),
							result.getUsername() != null ? run.getReport().getDomainName()+"/"+ result.getUsername().getValue() : null,
							emailTemplate,
							"en",
							playerPlaceholders(result, getExternalDomain(run.getReport().getDomainName())),
							null,
							null
						);
						run.setActionsPerformed((run.getActionsPerformed() != null)? run.getActionsPerformed() + 1: 1);
						run = reportRunRepository.save(run);
					}
				}
				page++;
				if (!results.hasNext()) doProcess = false;
			}
		}
	}
	
	private void sendSMS(String domainName, String cellphoneNumber, String userGuid, String smsTemplateName, String smsTemplateLang, Set<Placeholder> placeholders) {
		smsStream.process(SMSBasic.builder()
			.domainName(domainName)
			.smsTemplateName(smsTemplateName)
			.smsTemplateLang(smsTemplateLang)
			.to(cellphoneNumber)
			.userGuid(userGuid)
			.priority(2)
			.placeholders(placeholders)
			.build()
		);
	}
	
	private void sendEmail(String domainName, String emailAddress, String userGuid, String emailTemplateName, String emailTemplateLang,
			Set<Placeholder> placeholders, String attachmentName, byte[] attachmentData) throws IOException {
		mailStream.process(EmailData.builder()
				.authorSystem()
				.emailTemplateName(emailTemplateName)
				.emailTemplateLang(emailTemplateLang)
				.to(emailAddress)
				.userGuid(userGuid)
				.priority(2)
				.attachmentName(attachmentName)
				.attachmentData(attachmentData)
				.placeholders(placeholders)
				.domainName(domainName)
				.build()
		);
	}

	private String asSting(StringValue stringValue) {
		return ofNullable(stringValue)
				.map(StringValue::getValue)
				.orElse(null);
	}

	private Set<Placeholder> playerPlaceholders(ReportRunResults result, Domain domain) {
		Set<Placeholder> placeholders = new HashSet<>();
		placeholders.add(USER_NAME.from(asSting(result.getUsername())));
		placeholders.add(USER_EMAIL_ADDRESS.from(asSting(result.getEmail())));
		placeholders.add(USER_FIRST_NAME.from(asSting(result.getFirstName())));
		placeholders.add(USER_LAST_NAME.from(asSting(result.getLastName())));
		placeholders.add(USER_CREATE_DATE.from(result.getCreatedDate()));
		if (domain != null) {
			placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
		}
		return placeholders;
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
