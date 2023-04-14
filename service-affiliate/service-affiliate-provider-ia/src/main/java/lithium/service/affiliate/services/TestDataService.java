package lithium.service.affiliate.services;

import java.util.Date;

import lithium.service.affiliate.data.entities.Report;
import lithium.service.affiliate.data.entities.ReportAction;
import lithium.service.affiliate.data.entities.ReportFilter;
import lithium.service.affiliate.data.entities.ReportRevision;
import lithium.service.user.client.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.accounting.objects.Period;
import lithium.service.affiliate.data.repositories.ReportActionRepository;
import lithium.service.affiliate.data.repositories.ReportFilterRepository;
import lithium.service.affiliate.data.repositories.ReportRepository;
import lithium.service.affiliate.data.repositories.ReportRevisionRepository;
import lithium.service.affiliate.data.repositories.ReportRunRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestDataService {
	
	@Autowired
	ReportRepository reportRepository;
	@Autowired
	ReportRunRepository reportRunRepository;
	@Autowired
	ReportRevisionRepository reportRevisionRepository;
	@Autowired
	ReportFilterRepository reportFilterRepository;
	@Autowired
	ReportActionRepository reportActionRepository;
	@Autowired ReportActionLabelValueService reportActionLabelValueService;

	public void load() {

		log.info("Loading test data");
		
		if (reportRepository.findByCurrentNameAndDomainName("players_alltime", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.report(report)
					.cron("0 15 10 * * *")
					.name("players_alltime")
					.description("All Players / All Time")
					.granularity(Period.GRANULARITY_TOTAL)
					.granularityOffset(0)
					.updateBy(User.SYSTEM_GUID)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
		}
		
		/**
		 *	Daily report that lists all time data for all players where birthdate is today and status is active.
		 *	Send individual email using email template to every player. 
		 *	Send individual email template to a specified recipient(s). 
		 *	Send full report to specified recipient(s).
		 **/
		if (reportRepository.findByCurrentNameAndDomainName("players_birthday", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.cron("0 0 0 * * *")
					.report(report)
					.name("players_birthday")
					.description("Enabled Players Birthday / All Time")
					.granularity(Period.GRANULARITY_TOTAL)
					.granularityOffset(0)
					.updateBy(User.SYSTEM_GUID)
					.allFiltersApplicable(true)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_BIRTHDAY)
					.operator(ReportFilterService.OPERATOR_EQUAL_TO)
					.value("0")
					.build());
			ReportAction reportActionSendEmailToPlayer = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_EMAIL_TO_PLAYER)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendEmailToPlayer, ReportActionService.LABEL_REPORT_PLAYER_EMAIL_TEMPLATE, "player_birthday");
			ReportAction reportActionSendFullReportViaEmail = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE, "player_report_"+rev.getName());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.khan@playsafesa.com");
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.mk1@gmail.com");
		}
		
		/**
		 *	Daily report that lists all players that have deposited more than once and have logged in exactly 2 days ago. 
		 *	Send individual email using email template to every player.
		 *	Send individual email template to a specified recipient(s).
		 *	Send full report to specified recipient(s). 
		 **/
		if (reportRepository.findByCurrentNameAndDomainName("players_deposit_greaterThan1_loggedIn2DaysAgo", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.cron("0 0 1 * * *")
					.report(report)
					.name("players_deposit_greaterThan1_loggedIn2DaysAgo")
					.description("Enabled Players with > 1 Deposits / Last Logged in 2 Days Ago / All Time")
					.granularity(Period.GRANULARITY_TOTAL)
					.granularityOffset(0)
					.updateBy(User.SYSTEM_GUID)
					.allFiltersApplicable(true)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_DEPOSIT_COUNT)
					.operator(ReportFilterService.OPERATOR_GREATER_THAN)
					.value("1")
					.build());
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_LAST_LOGIN_DATE)
					.operator(ReportFilterService.OPERATOR_EQUAL_TO)
					.value("2")
					.build());
			ReportAction reportActionSendEmailToPlayer = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_EMAIL_TO_PLAYER)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendEmailToPlayer, ReportActionService.LABEL_REPORT_PLAYER_EMAIL_TEMPLATE, "player_account_inactive_2days");
			ReportAction reportActionSendFullReportViaEmail = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE, "player_report_"+rev.getName());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.khan@playsafesa.com");
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.mk1@gmail.com");
		}
		
		/**
		 * 	Weekly report that lists previous week's player data where bet amount for the week is > 1000.
		 * 	Send full report to specified recipient(s).
		 **/
		if (reportRepository.findByCurrentNameAndDomainName("players_betAmount_greaterThan1000", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.cron("0 0 2 * * MON")
					.report(report)
					.name("players_betAmount_greaterThan1000")
					.description("Enabled Players with > 1000 Bet Amount / Weekly")
					.granularity(Period.GRANULARITY_WEEK)
					.granularityOffset(1)
					.updateBy(User.SYSTEM_GUID)
					.allFiltersApplicable(true)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_CASINO_BET_AMOUNT_CENTS)
					.operator(ReportFilterService.OPERATOR_GREATER_THAN)
					.value("100000")
					.build());
			ReportAction reportActionSendFullReportViaEmail = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE, "player_report_"+rev.getName());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.khan@playsafesa.com");
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.mk1@gmail.com");
		}
		
		/**
		 *	Daily report that lists all time data for players where signup = 2 days ago and deposit count = 0.
		 *	Send individual email using email template to every player.
		 *	Send individual email template to a specified recipient(s).
		 *	Send full report to specified recipient(s).
		 **/
		if (reportRepository.findByCurrentNameAndDomainName("players_signup2DaysAgo_deposit0", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.report(report)
					.cron("0 0 3 * * *")
					.name("players_signup2DaysAgo_deposit0")
					.description("Enabled Players with Signup 2 Days Ago / 0 Deposits / All Time")
					.granularity(Period.GRANULARITY_TOTAL)
					.granularityOffset(0)
					.updateBy(User.SYSTEM_GUID)
					.allFiltersApplicable(true)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_CREATED_DATE)
					.operator(ReportFilterService.OPERATOR_EQUAL_TO)
					.value("2")
					.build());
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_DEPOSIT_COUNT)
					.operator(ReportFilterService.OPERATOR_EQUAL_TO)
					.value("0")
					.build());
			ReportAction reportActionSendEmailToPlayer = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_EMAIL_TO_PLAYER)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendEmailToPlayer, ReportActionService.LABEL_REPORT_PLAYER_EMAIL_TEMPLATE, "player_account_signup_2days_dep_0");
			ReportAction reportActionSendFullReportViaEmail = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE, "player_report_"+rev.getName());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.khan@playsafesa.com");
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.mk1@gmail.com");
		}
		
		/**
		 * 	Monthly report that lists previous month data for players where $ deposit > $1000 and $ deposit < $2000.
		 * 	Send full report to specified recipient(s).
		 **/
		if (reportRepository.findByCurrentNameAndDomainName("players_deposit_greaterThan1000_lessThan2000", "luckybetz").isEmpty()) {
			Report report = Report.builder()
					.domainName("luckybetz")
					.createdBy(User.SYSTEM_GUID)
					.scheduledDate(new Date())
					.enabled(true)
					.build();
			report = reportRepository.save(report);
			ReportRevision rev = ReportRevision.builder()
					.report(report)
					.cron("0 0 4 1 * *")
					.name("players_deposit_greaterThan1000_lessThan2000")
					.description("Enabled Players with Deposit Amount > $1000 < $2000 / Monthly")
					.granularity(Period.GRANULARITY_MONTH)
					.granularityOffset(1)
					.updateBy(User.SYSTEM_GUID)
					.allFiltersApplicable(true)
					.build();
			rev = reportRevisionRepository.save(rev);
			report.setCurrent(rev);
			reportRepository.save(report);
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_DEPOSIT_AMOUNT_CENTS)
					.operator(ReportFilterService.OPERATOR_GREATER_THAN)
					.value("100000")
					.build());
			reportFilterRepository.save(ReportFilter.builder()
					.reportRevision(rev)
					.field(ReportFilterService.FIELD_PLAYER_DEPOSIT_AMOUNT_CENTS)
					.operator(ReportFilterService.OPERATOR_LESS_THAN)
					.value("200000")
					.build());
			ReportAction reportActionSendFullReportViaEmail = reportActionRepository.save(ReportAction.builder()
					.reportRevision(rev)
					.actionType(ReportActionService.REPORT_ACTION_SEND_FULL_REPORT_VIA_EMAIL)
					.build());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_EMAIL_TEMPLATE, "player_report_"+rev.getName());
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.khan@playsafesa.com");
			reportActionLabelValueService
				.findOrCreate(reportActionSendFullReportViaEmail, ReportActionService.LABEL_REPORT_FULL_RECIPIENT_EMAIL, "reza.mk1@gmail.com");
		}

//		if (reportRepository.findByNameAndDomainName("players_yesterday", "luckybet") == null) {
//			Report report = Report.builder().name("players_yesterday").createdBy("system")
//					.description("All Players / Yesterday's Numbers")
//					.domainName("luckybet")
//					.cron("0 15 10 * * *")
//					.scheduledDate(new Date())
//					.enabled(true)
//					.granularity(Period.GRANULARITY_DAY)
//					.granularityOffset(1)
//					.build();
//			reportRepository.save(report);
//		}
//
//		if (reportRepository.findByNameAndDomainName("players_lastmonth", "luckybetz") == null) {
//			Report report = Report.builder().name("players_lastmonth").createdBy("system")
//					.description("All Players / Last Month's Numbers")
//					.domainName("luckybetz")
//					.cron("0 0 1 1 * *")
//					.scheduledDate(new Date())
//					.enabled(true)
//					.granularity(Period.GRANULARITY_MONTH)
//					.granularityOffset(1)
//					.build();
//			reportRepository.save(report);
//		}

//		if (reportRepository.findByNameAndDomainName("players every 5 minutes", "luckybetz") != null) return;
//
//		report = Report.builder().name("players every 5 minutes").createdBy("system")
//				.description("All Players")
//				.domainName("luckybetz")
//				.cron("0 */3 * * * *")
//				.enabled(true)
//				.build();
//		
//		reportRepository.save(report);
	}
	
}
