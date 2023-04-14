package lithium.service.report.players.services;

import lithium.service.Response;
import lithium.service.access.client.gamstop.GamStopStream;
import lithium.service.access.client.gamstop.objects.BatchExclusionCheckRequest;
import lithium.service.access.client.gamstop.objects.ExclusionRequest;
import lithium.service.access.client.gamstop.objects.ExclusionRequestLine;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Optional.ofNullable;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_BALANCE_ADJUST_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_BALANCE_ADJUST_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BET_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BET_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_ACTIVATE_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_BET_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_BET_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_CANCEL_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_EXPIRE_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_MAX_PAYOUT_EXCESS_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_NET_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_PENDING_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_PENDING_CANCEL_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_PENDING_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_PENDING_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_PENDING_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_WIN_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_BONUS_WIN_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_NET_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_WIN_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CASINO_WIN_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_COMPLETED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CURRENT_BALANCE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CURRENT_BALANCE_CASINO_BONUS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_CURRENT_BALANCE_CASINO_BONUS_PENDING;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_DEPOSIT_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_DEPOSIT_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PAYOUT_AMOUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PAYOUT_COUNT;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_CLOSING_BALANCE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS_PENDING;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_OPENING_BALANCE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS_PENDING;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_PROCESSED_RECORDS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_BY;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_STARTED_ON;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.REPORT_TOTAL_RECORDS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_CREATE_DATE;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_DOB;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_EMAIL_ADDRESS;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_FIRST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_LAST_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_NAME;
import static lithium.service.client.objects.placeholders.PlaceholderBuilder.USER_OPT_OUT_EMAIL_URL;

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
	@Autowired NotificationStream notificationStream;
	@Autowired GamStopStream gamStopStream;

	@Value("${lithium.services.report.players.gamstop-page-size:100}")
	private int gamstopPageSize;

	public static final String REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL = "sendFullReportViaEmail";
	public static final String REPORT_ACTION_SEND_REPORT_STATS_VIA_EMAIL = "sendReportStatsViaEmail";
	public static final String REPORT_ACTION_SEND_EMAIL_TO_PLAYER = "sendEmailToPlayer";
	public static final String REPORT_ACTION_SEND_SMS_TO_PLAYER = "sendSMSToPlayer";
	public static final String REPORT_ACTION_SEND_NOTIFICATION_TO_PLAYER = "sendNotificationToPlayer";
	public static final String REPORT_ACTION_CHECK_GAMSTOP_STATUS = "checkGamstopStatus";

	public static final String LABEL_REPORT_FULL_RECIPIENT_EMAIL = "reportFullRecipientEmail";
	public static final String LABEL_REPORT_STATS_RECIPIENT_EMAIL = "reportStatsRecipientEmail";
	public static final String LABEL_REPORT_FULL_EMAIL_TEMPLATE = "reportFullEmailTemplate";
	public static final String LABEL_REPORT_STATS_EMAIL_TEMPLATE = "reportStatsEmailTemplate";
	public static final String LABEL_REPORT_PLAYER_EMAIL_TEMPLATE = "reportPlayerEmailTemplate";
	public static final String LABEL_REPORT_PLAYER_SMS_TEMPLATE = "reportPlayerSMSTemplate";
	public static final String LABEL_REPORT_PLAYER_NOTIFICATION_NAME = "reportPlayerNotificationName";
	public static final String LABEL_REPORT_FULL_GENERATION_FAILED_EMAIL_TEMPLATE = "email.failed.report.players";
	
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
					case ReportActionService.REPORT_ACTION_SEND_NOTIFICATION_TO_PLAYER:
						sendNotificationToPlayer(run);
						break;
					case ReportActionService.REPORT_ACTION_CHECK_GAMSTOP_STATUS:
						checkGamstopStatus(run);
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

					if ((!result.getStatus().getValue().startsWith("disabled")) &&
						(!result.getStatus().getValue().startsWith("suspend")) &&
						(!result.getSmsOptOut())) {
						sendSMS(
							run.getReport().getDomainName(),
							result.getCellphoneNumber().getValue(),
							result.getUserGuid(),
							smsTemplate,
							"en",
							playerPlaceholders(result, getExternalDomain(run.getReport().getDomainName()))
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
	
	public void sendNotificationToPlayer(ReportRun run) {
		Label lblNotificationName = labelService.findOrCreate(LABEL_REPORT_PLAYER_NOTIFICATION_NAME);
		List<ReportActionLabelValue> notificationNameList = reportActionLabelValueRepository
				.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), lblNotificationName);
		String notificationName = (notificationNameList.size() > 0)? notificationNameList.get(0).getLabelValue().getValue(): "";
		if (notificationName != null && !notificationName.isEmpty()) {
			boolean doProcess = true;
			int page = 0;
			while (doProcess) {
				PageRequest pr = PageRequest.of(page, 10);
				Page<ReportRunResults> results = reportRunResultsRepository.findByReportRunId(run.getId(), pr);
				for (ReportRunResults result: results) {
					if ((!result.getStatus().getValue().startsWith("disabled")) &&
						(!result.getStatus().getValue().startsWith("suspend"))) {
						notificationStream.process(
							UserNotification.builder()
							.userGuid(result.getUserGuid())
							.notificationName(notificationName)
							.build()
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
						if ((!result.getStatus().getValue().startsWith("disabled")) &&
							(!result.getStatus().getValue().startsWith("suspend")) &&
							(!result.getEmailOptOut())) {
							sendEmail(
								run.getReport().getDomainName(),
								result.getEmail().getValue(),
									result.getUserGuid(),
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

	private Set<Placeholder> playerPlaceholders(ReportRunResults result, Domain domain) throws UnsupportedEncodingException {
		Set<Placeholder> placeholders = new HashSet<>();
		placeholders.add(USER_NAME.from(asSting(result.getUsername())));
		placeholders.add(USER_EMAIL_ADDRESS.from(asSting(result.getEmail())));
		placeholders.add(USER_FIRST_NAME.from(asSting(result.getFirstName())));
		placeholders.add(USER_LAST_NAME.from(asSting(result.getLastName())));
		placeholders.add(USER_CREATE_DATE.from(result.getCreatedDate()));
		placeholders.add(USER_DOB.from(result.getDateOfBirth()));
		placeholders.add(REPORT_CURRENT_BALANCE.from(fromCents(result.getCurrentBalanceCents())));
		placeholders.add(REPORT_CURRENT_BALANCE_CASINO_BONUS.from(fromCents(result.getCurrentBalanceCasinoBonusCents())));
		placeholders.add(REPORT_CURRENT_BALANCE_CASINO_BONUS_PENDING.from(fromCents(result.getCurrentBalanceCasinoBonusPendingCents())));
		placeholders.add(REPORT_PERIOD_OPENING_BALANCE.from(fromCents(result.getPeriodOpeningBalanceCents())));
		placeholders.add(REPORT_PERIOD_CLOSING_BALANCE.from(fromCents(result.getPeriodClosingBalanceCents())));
		placeholders.add(REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS.from(fromCents(result.getPeriodOpeningBalanceCasinoBonusCents())));
		placeholders.add(REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS.from(fromCents(result.getPeriodClosingBalanceCasinoBonusCents())));
		placeholders.add(REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS_PENDING.from(fromCents(result.getPeriodOpeningBalanceCasinoBonusPendingCents())));
		placeholders.add(REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS_PENDING.from(fromCents(result.getPeriodClosingBalanceCasinoBonusPendingCents())));
		placeholders.add(REPORT_DEPOSIT_AMOUNT.from(fromCents(result.getDepositAmountCents())));
		placeholders.add(REPORT_DEPOSIT_COUNT.from(result.getDepositCount()));
		placeholders.add(REPORT_PAYOUT_AMOUNT.from(fromCents(result.getPayoutAmountCents())));
		placeholders.add(REPORT_PAYOUT_COUNT.from(result.getPayoutCount()));
		placeholders.add(REPORT_BALANCE_ADJUST_AMOUNT.from(fromCents(result.getBalanceAdjustAmountCents())));
		placeholders.add(REPORT_BALANCE_ADJUST_COUNT.from(result.getBalanceAdjustCount()));
		placeholders.add(REPORT_CASINO_BET_AMOUNT.from(fromCents(result.getCasinoBetAmountCents())));
		placeholders.add(REPORT_CASINO_BET_COUNT.from(result.getCasinoBetCount()));
		placeholders.add(REPORT_CASINO_WIN_AMOUNT.from(fromCents(result.getCasinoWinAmountCents())));
		placeholders.add(REPORT_CASINO_WIN_COUNT.from(result.getCasinoWinCount()));
		placeholders.add(REPORT_CASINO_NET_AMOUNT.from(fromCents(result.getCasinoNetAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_BET_AMOUNT.from(fromCents(result.getCasinoBonusBetAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_BET_COUNT.from(result.getCasinoBonusBetCount()));
		placeholders.add(REPORT_CASINO_BONUS_WIN_AMOUNT.from(fromCents(result.getCasinoBonusWinAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_WIN_COUNT.from(result.getCasinoBonusWinCount()));
		placeholders.add(REPORT_CASINO_BONUS_NET_AMOUNT.from(fromCents(result.getCasinoBonusNetAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_PENDING_AMOUNT.from(fromCents(result.getCasinoBonusPendingAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_PENDING_AMOUNT.from(fromCents(result.getCasinoBonusTransferToBonusPendingAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_PENDING_AMOUNT.from(fromCents(result.getCasinoBonusTransferFromBonusPendingAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_PENDING_CANCEL_AMOUNT.from(fromCents(result.getCasinoBonusPendingCancelAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_PENDING_COUNT.from(result.getCasinoBonusPendingCount()));
		placeholders.add(REPORT_CASINO_BONUS_ACTIVATE_AMOUNT.from(fromCents(result.getCasinoBonusActivateAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_AMOUNT.from(fromCents(result.getCasinoBonusTransferToBonusAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_AMOUNT.from(fromCents(result.getCasinoBonusTransferFromBonusAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_CANCEL_AMOUNT.from(fromCents(result.getCasinoBonusCancelAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_EXPIRE_AMOUNT.from(fromCents(result.getCasinoBonusExpireAmountCents())));
		placeholders.add(REPORT_CASINO_BONUS_MAX_PAYOUT_EXCESS_AMOUNT.from(fromCents(result.getCasinoBonusMaxPayoutExcessAmountCents())));
		if (domain != null) {
			placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
		}
		StringBuilder sb = new StringBuilder();
		sb.append("userid="+result.getUserId());
		sb.append("&guid="+result.getUserGuid());
		sb.append("&fullName="+result.getFirstName().getValue()+" "+result.getLastName().getValue());
		sb.append("&email="+result.getEmail().getValue());
		sb.append("&cell="+result.getCellphoneNumber().getValue());
		sb.append("&optout=true");
		sb.append("&method=email");
		String optOutEmailUrl = domain.getUrl() + "?action=optout&h=" + Base64.getEncoder().encodeToString(sb.toString().getBytes("UTF-8"));
		placeholders.add(USER_OPT_OUT_EMAIL_URL.from(optOutEmailUrl));
		return placeholders;
	}

	public static Optional<String> fromCents(Long amount) {
		return ofNullable(amount).map(aLong->new BigDecimal(aLong).movePointLeft(2).toPlainString());
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

	private void checkGamstopStatus(ReportRun run) throws IOException {
		boolean doProcess = true;
		int page = 0;
		while (doProcess) {
			PageRequest pr = PageRequest.of(page, gamstopPageSize);
			Page<ReportRunResults> results = reportRunResultsRepository.findByReportRunId(run.getId(), pr);
			int count = 0;
			List<ExclusionRequestLine> requestData = new ArrayList<>();
			BatchExclusionCheckRequest request = BatchExclusionCheckRequest
					.builder()
					.reportId(run.getReport().getId())
					.reportRunId(run.getId())
					.requestData(requestData)
					.domainName(run.getReport().getDomainName())
					.build();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			for (ReportRunResults result: results) {
				if (result.getDateOfBirth() == null) {
					log.error("Invalid date for {}", result);
					continue;
				}
				ExclusionRequest exclusionRequest = ExclusionRequest.builder()
						.firstName(result.getFirstName().getValue())
						.lastName(result.getLastName().getValue())
						.mobile(result.getCellphoneNumber().getValue())
						.postcode(result.getResidentialAddressPostalCode().getValue())
						.email(result.getEmail().getValue())
						.dateOfBirth(df.format(result.getDateOfBirth()))
						.correlationId(result.getUserGuid())
						.build();
				ExclusionRequestLine requestLine = ExclusionRequestLine.builder()
						.reportRunResultsId(result.getId())
						.exclusionRequest(exclusionRequest)
						.username(result.getUsername().getValue())
						.userGuid(result.getUserGuid())
						.build();
				requestData.add(requestLine);
				count++;
			}
			if(!requestData.isEmpty()) {
				updateSelfExclusionStatus(request);
			}
			run.setActionsPerformed((run.getActionsPerformed() != null)? run.getActionsPerformed() + count : count);
			run = reportRunRepository.save(run);
			page++;
			if (!results.hasNext()) doProcess = false;
		}
	}

	private void updateSelfExclusionStatus(BatchExclusionCheckRequest request) {
		gamStopStream.process(request);
	}

	public void sendReportFailedToCompleteViaEmail(ReportRun run) throws IOException, LithiumServiceClientFactoryException {

		Label label = labelService.findOrCreate(LABEL_REPORT_FULL_RECIPIENT_EMAIL);
			List<ReportActionLabelValue> rrlvs = reportActionLabelValueRepository
					.findByReportActionReportRevisionAndLabelValueLabel(run.getReportRevision(), label);
			for (ReportActionLabelValue rrlv: rrlvs) {
				sendEmail(
						run.getReport().getDomainName(),
						rrlv.getLabelValue().getValue(),
						null,
						LABEL_REPORT_FULL_GENERATION_FAILED_EMAIL_TEMPLATE,
						LocaleContextHolder.getLocale().getLanguage(),
						reportEmailPlaceholders(run, getExternalDomain(run.getReport().getDomainName())),
						null,
						null
				);
			}
	}
}
