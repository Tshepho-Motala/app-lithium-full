package lithium.service.affiliate.services;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccount;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.affiliate.data.entities.*;
import lithium.service.affiliate.data.repositories.ReportFilterRepository;
import lithium.service.affiliate.data.repositories.ReportRepository;
import lithium.service.affiliate.data.repositories.ReportRunRepository;
import lithium.service.affiliate.data.repositories.ReportRunResultsRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.UserClient;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportRunService {

	@Autowired
	ReportRepository reportRepository;
	@Autowired
	ReportRunRepository reportRunRepository;
	@Autowired
	ReportRunResultsRepository reportRunResultsRepository;
	@Autowired
	ReportFilterRepository reportFilterRepository;
	@Autowired ReportFilterService reportFilterService;
	@Autowired ReportActionService reportActionService;
	@Autowired StringValueService svs;
	@Autowired CachingDomainClientService currencyService;
	
	@Autowired LithiumServiceClientFactory services;
	
	private void run(ReportRun reportRun) throws Exception {
		
		UserClient userClient = services.target(UserClient.class);
		AccountingPeriodClient accountingPeriodClient = services.target(AccountingPeriodClient.class);
		AccountingSummaryAccountClient accountingSummaryAccountClient = services.target(AccountingSummaryAccountClient.class);
		AccountingSummaryTransactionTypeClient accountingSummaryTranTypeClient = services.target(AccountingSummaryTransactionTypeClient.class);
		
		ReportRevision rev = reportRun.getReportRevision();
		List<ReportFilter> reportFilters = reportFilterRepository.findByReportRevision(rev);

		Period period = accountingPeriodClient.findByOffset(reportRun.getReport().getDomainName(), 
				rev.getGranularity(), rev.getGranularityOffset()).getData();
		
		log.info("Period for report " + period + " " + reportRun);
		reportRun.setPeriodStartDate(period.getDateStart());
		reportRun = reportRunRepository.save(reportRun);
		boolean more = true;
		Long count = 0L;
		Long pos = 0L;
		
		Long totalRecords = 0L, filteredRecords = 0L, processedRecords = 0L;
		while (more) {
			DataTableResponse<User> response = userClient.table(reportRun.getReport().getDomainName(), "1", pos, 1L, null, null, Label.AFFILIATE_GUID_LABEL, null);
			totalRecords = response.getRecordsTotal();
			
			reportRun.setTotalRecords(totalRecords);
			reportRun = reportRunRepository.save(reportRun);
			
			count = count + response.getData().size();
			
			pos = pos + response.getData().size();
			if (count >= response.getRecordsTotal()) more = false;
			
			userLoop:
			for (User user: response.getData()) {
				log.debug("Got user " + user);
				
				String guid = reportRun.getReport().getDomainName().toLowerCase() + "/" + user.getUsername().toLowerCase();
				
				ReportRunResults row = ReportRunResults.builder().reportRun(reportRun).build();

				row.setUserId(user.getId());
				row.setUsername(svs.link(user.getUsername().toLowerCase()));
				row.setEmail(svs.link((user.getEmail() != null)? user.getEmail().toLowerCase() : null));
				row.setFirstName(svs.link(user.getFirstName()));
				row.setLastName(svs.link(user.getLastName()));
				if (user.getStatus() != null) {
					row.setEnabled(user.getStatus().getUserEnabled());
					row.setStatus(svs.link(user.getStatus().getName()));
				}

				row.setEmailValidated(user.isEmailValidated());

				if (user.getResidentialAddress() != null) {
					row.setResidentialAddressLine1(svs.link(user.getResidentialAddress().getAddressLine1()));
					row.setResidentialAddressLine2(svs.link(user.getResidentialAddress().getAddressLine2()));
					row.setResidentialAddressLine3(svs.link(user.getResidentialAddress().getAddressLine3()));
					row.setResidentialAddressCity(svs.link(user.getResidentialAddress().getCity()));
					row.setResidentialAddressAdminLevel1(svs.link(user.getResidentialAddress().getAdminLevel1()));
					row.setResidentialAddressCountry(svs.link(user.getResidentialAddress().getCountry()));
					row.setResidentialAddressPostalCode(svs.link(user.getResidentialAddress().getPostalCode()));
				} else {
					row.setResidentialAddressLine1(svs.link(null));
					row.setResidentialAddressLine2(svs.link(null));
					row.setResidentialAddressLine3(svs.link(null));
					row.setResidentialAddressCity(svs.link(null));
					row.setResidentialAddressAdminLevel1(svs.link(null));
					row.setResidentialAddressCountry(svs.link(null));
					row.setResidentialAddressPostalCode(svs.link(null));
				}
				
				if (user.getPostalAddress() != null) {
					row.setPostalAddressLine1(svs.link(user.getPostalAddress().getAddressLine1()));
					row.setPostalAddressLine2(svs.link(user.getPostalAddress().getAddressLine2()));
					row.setPostalAddressLine3(svs.link(user.getPostalAddress().getAddressLine3()));
					row.setPostalAddressCity(svs.link(user.getPostalAddress().getCity()));
					row.setPostalAddressAdminLevel1(svs.link(user.getPostalAddress().getAdminLevel1()));
					row.setPostalAddressCountry(svs.link(user.getPostalAddress().getCountry()));
					row.setPostalAddressPostalCode(svs.link(user.getPostalAddress().getPostalCode()));
				} else {
					row.setPostalAddressLine1(svs.link(null));
					row.setPostalAddressLine2(svs.link(null));
					row.setPostalAddressLine3(svs.link(null));
					row.setPostalAddressCity(svs.link(null));
					row.setPostalAddressAdminLevel1(svs.link(null));
					row.setPostalAddressCountry(svs.link(null));
					row.setPostalAddressPostalCode(svs.link(null));	
				}
				
				row.setTelephoneNumber(svs.link(user.getTelephoneNumber()));
				row.setCellphoneNumber(svs.link(user.getCellphoneNumber()));
				
				row.setCreatedDate(user.getCreatedDate());
				row.setUpdatedDate(user.getUpdatedDate());
				
				row.setSignupBonusCode(svs.link(user.getBonusCode()));
				
				Map<String, String> userLabelMap = user.getLabelAndValue();
				if (userLabelMap != null && !userLabelMap.isEmpty()) {
					if (userLabelMap.containsKey(Label.AFFILIATE_GUID_LABEL)) {
						row.setAffiliateGuid(svs.link(userLabelMap.get(Label.AFFILIATE_GUID_LABEL)));
					} else {
						row.setAffiliateGuid(svs.link(""));
					}
					
					if (userLabelMap.containsKey(Label.AFFILIATE_SECONDARY_GUID_1_LABEL)) {
						row.setBannerGuid(svs.link(userLabelMap.get(Label.AFFILIATE_SECONDARY_GUID_1_LABEL)));
					} else {
						row.setBannerGuid(svs.link(""));
					}
					
					if (userLabelMap.containsKey(Label.AFFILIATE_SECONDARY_GUID_2_LABEL)) {
						row.setCampaignGuid(svs.link(userLabelMap.get(Label.AFFILIATE_SECONDARY_GUID_2_LABEL)));
					} else {
						row.setCampaignGuid(svs.link(""));
					}
				} else {
					row.setAffiliateGuid(svs.link(""));
					row.setBannerGuid(svs.link(""));
					row.setCampaignGuid(svs.link(""));
				}
				
				if (user.getDobYear() != null) row.setDateOfBirthYear(user.getDobYear());
				if (user.getDobMonth() != null) row.setDateOfBirthMonth(user.getDobMonth());
				if (user.getDobDay() != null) row.setDateOfBirthDay(user.getDobDay());

				if (user.getDobDay() != null && user.getDobMonth() != null && user.getDobYear() != null) {
					row.setDateOfBirth(new DateTime(user.getDobYear(), user.getDobMonth(), user.getDobDay(), 0, 0, 0).toDate());	
				}

				row.setEmailOptOut((user.getEmailOptOut() != null)? user.getEmailOptOut(): false);
				row.setSmsOptOut((user.getSmsOptOut() != null)? user.getSmsOptOut(): false);
				row.setCallOptOut((user.getCallOptOut() != null)? user.getCallOptOut(): false);

				Long playerBalanceAccountId = null;
				Long playerBalanceCasinoBonusAccountId = null;
				Long playerBalanceCasinoBonusPendingAccountId = null;
				Domain domain = currencyService.retrieveDomainFromDomainService(reportRun.getReport().getDomainName());
				
				Response<SummaryAccount> summaryAccountResponse = accountingSummaryAccountClient.find(period.getId(), "PLAYER_BALANCE", "PLAYER_BALANCE", domain.getCurrency(), guid);
				SummaryAccount playerBalanceSummary = summaryAccountResponse.getData();
				if (playerBalanceSummary != null) {
					playerBalanceAccountId = playerBalanceSummary.getAccount().getId();
					row.setCurrentBalanceCents(playerBalanceSummary.getAccount().getBalanceCents() * -1);
					row.setPeriodOpeningBalanceCents(playerBalanceSummary.getOpeningBalanceCents() * -1);
					row.setPeriodClosingBalanceCents(playerBalanceSummary.getClosingBalanceCents() * -1);
				}

				summaryAccountResponse = accountingSummaryAccountClient.find(period.getId(), "PLAYER_BALANCE_CASINO_BONUS", "PLAYER_BALANCE", domain.getCurrency(), guid);
				SummaryAccount playerBalanceCasinoBonusSummary = summaryAccountResponse.getData();
				if (playerBalanceCasinoBonusSummary != null) {
					playerBalanceCasinoBonusAccountId = playerBalanceCasinoBonusSummary.getAccount().getId();
					row.setCurrentBalanceCasinoBonusCents(playerBalanceCasinoBonusSummary.getAccount().getBalanceCents() * -1);
					row.setPeriodOpeningBalanceCasinoBonusCents(playerBalanceCasinoBonusSummary.getOpeningBalanceCents() * -1);
					row.setPeriodClosingBalanceCasinoBonusCents(playerBalanceCasinoBonusSummary.getClosingBalanceCents() * -1);
				}
				
				summaryAccountResponse = accountingSummaryAccountClient.find(period.getId(), "PLAYER_BALANCE_CASINO_BONUS_PENDING", "PLAYER_BALANCE", domain.getCurrency(), guid);
				SummaryAccount playerBalanceCasinoBonusPendingSummary = summaryAccountResponse.getData();
				if (playerBalanceCasinoBonusPendingSummary != null) {
					playerBalanceCasinoBonusPendingAccountId = playerBalanceCasinoBonusPendingSummary.getAccount().getId();
					row.setCurrentBalanceCasinoBonusPendingCents(playerBalanceCasinoBonusPendingSummary.getAccount().getBalanceCents() * -1);
					row.setPeriodOpeningBalanceCasinoBonusPendingCents(playerBalanceCasinoBonusPendingSummary.getOpeningBalanceCents() * -1);
					row.setPeriodClosingBalanceCasinoBonusPendingCents(playerBalanceCasinoBonusPendingSummary.getClosingBalanceCents() * -1);
				}
				
				if (playerBalanceAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASHIER_DEPOSIT");
					SummaryAccountTransactionType d = r.getData(); 
					row.setDepositAmountCents((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
					row.setDepositCount((d == null)? 0L : d.getTranCount());
				}

				if (playerBalanceAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASHIER_DEPOSIT_FEE");
					SummaryAccountTransactionType d = r.getData();
					row.setDepositFeeCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}

				if (playerBalanceAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASHIER_PAYOUT");
					SummaryAccountTransactionType d = r.getData(); 
					row.setPayoutAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
					row.setPayoutCount((d == null)? 0L : d.getTranCount());
				}
				
				long balanceAdjustsAmount = 0L;
				long balanceAdjustsCount = 0L;

				if (playerBalanceAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "BALANCE_ADJUST");
					SummaryAccountTransactionType d = r.getData(); 
					balanceAdjustsAmount += ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
					balanceAdjustsCount += ((d == null)? 0L : d.getTranCount());
				}

				if (playerBalanceCasinoBonusAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "BALANCE_ADJUST");
					SummaryAccountTransactionType d = r.getData(); 
					balanceAdjustsAmount += ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
					balanceAdjustsCount += ((d == null)? 0L : d.getTranCount());
				}
				
				row.setBalanceAdjustAmountCents(balanceAdjustsAmount);
				row.setBalanceAdjustCount(balanceAdjustsCount);

				{ // Isolate main balance casino activity
					long casinoBetAmount = 0L;
					long casinoBetCount = 0L;
					
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_BET");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount += ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoBetCount += ((d == null)? 0L : d.getTranCount());
					}
	
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_BET_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount -= ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoBetCount -= ((d == null)? 0L : d.getTranCount());
					}
					
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_NEGATIVE_BET");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount -= ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoBetCount -= ((d == null)? 0L : d.getTranCount());
					}
					
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_NEGATIVE_BET_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount += ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoBetCount += ((d == null)? 0L : d.getTranCount());
					}
	
					row.setCasinoBetAmountCents(casinoBetAmount);
					row.setCasinoBetCount(casinoBetCount);
					
					long casinoWinAmount = 0L;
					long casinoWinCount = 0L;
					
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_WIN");
						SummaryAccountTransactionType d = r.getData(); 
						casinoWinAmount += ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoWinCount += ((d == null)? 0L : d.getTranCount());
					}
	
					if (playerBalanceAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "CASINO_WIN_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoWinAmount -= ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoWinCount -= ((d == null)? 0L : d.getTranCount());
					}
	
					row.setCasinoWinAmountCents(casinoWinAmount);
					row.setCasinoWinCount(casinoWinCount);
					
					row.setCasinoNetAmountCents(casinoBetAmount - casinoWinAmount);
				}
			
				{ // Isolate casino balance casino activity
					long casinoBetAmount = 0L;
					long casinoBetCount = 0L;
					
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BET");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount += ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoBetCount += ((d == null)? 0L : d.getTranCount());
					}
	
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BET_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount -= ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoBetCount -= ((d == null)? 0L : d.getTranCount());
					}
					
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_NEGATIVE_BET");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount -= ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoBetCount -= ((d == null)? 0L : d.getTranCount());
					}
					
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_NEGATIVE_BET_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoBetAmount += ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoBetCount += ((d == null)? 0L : d.getTranCount());
					}
	
					row.setCasinoBonusBetAmountCents(casinoBetAmount);
					row.setCasinoBonusBetCount(casinoBetCount);
					
					long casinoWinAmount = 0L;
					long casinoWinCount = 0L;
					
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_WIN");
						SummaryAccountTransactionType d = r.getData(); 
						casinoWinAmount += ((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
						casinoWinCount += ((d == null)? 0L : d.getTranCount());
					}
	
					if (playerBalanceCasinoBonusAccountId != null) {
						Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_WIN_ROLLBACK");
						SummaryAccountTransactionType d = r.getData(); 
						casinoWinAmount -= ((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
						casinoWinCount -= ((d == null)? 0L : d.getTranCount());
					}
	
					row.setCasinoBonusWinAmountCents(casinoWinAmount);
					row.setCasinoBonusWinCount(casinoWinCount);
					
					row.setCasinoBonusNetAmountCents(casinoBetAmount - casinoWinAmount);
				}
				
				long casinoBonusPendingCount = 0L;
				
				if (playerBalanceCasinoBonusPendingAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusPendingAccountId, period.getId(), "TRANSFER_TO_CASINO_BONUS_PENDING");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusTransferToBonusPendingAmountCents((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
					casinoBonusPendingCount += ((d == null)? 0L : d.getTranCount());
				}
				
				if (playerBalanceCasinoBonusPendingAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusPendingAccountId, period.getId(), "TRANSFER_FROM_CASINO_BONUS_PENDING");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusTransferFromBonusPendingAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
					casinoBonusPendingCount -= ((d == null)? 0L : d.getTranCount());
				}
				
				if (playerBalanceCasinoBonusPendingAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusPendingAccountId, period.getId(), "CASINO_BONUS_PENDING"); //free money for pending bonus
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusPendingAmountCents((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
				}
				
				if (playerBalanceCasinoBonusPendingAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusPendingAccountId, period.getId(), "CASINO_BONUS_PENDING_CANCEL");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusPendingCancelAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}
				
				row.setCasinoBonusPendingCount(casinoBonusPendingCount);

				if (playerBalanceAccountId != null) {
					//This will always be debit cents
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "TRANSFER_TO_CASINO_BONUS");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusTransferToBonusAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}
				
				if (playerBalanceAccountId != null) {
					//This will always be credit cents
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceAccountId, period.getId(), "TRANSFER_FROM_CASINO_BONUS");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusTransferFromBonusAmountCents((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
				}
			
				if (playerBalanceCasinoBonusAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BONUS_ACTIVATE"); //free money for active bonus
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusActivateAmountCents((d == null)? 0L : d.getCreditCents() - d.getDebitCents());
				}

				if (playerBalanceCasinoBonusAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BONUS_CANCEL");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusCancelAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}

				if (playerBalanceCasinoBonusAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BONUS_EXPIRED");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusExpireAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}

				if (playerBalanceCasinoBonusAccountId != null) {
					Response<SummaryAccountTransactionType> r = accountingSummaryTranTypeClient.find(playerBalanceCasinoBonusAccountId, period.getId(), "CASINO_BONUS_MAXPAYOUT_EXCESS");
					SummaryAccountTransactionType d = r.getData(); 
					row.setCasinoBonusMaxPayoutExcessAmountCents((d == null)? 0L : d.getDebitCents() - d.getCreditCents());
				}

				row.setNgrAmount(calculateNGRAmountCents(row.getCasinoNetAmountCents(), row.getCasinoBonusTransferToBonusAmountCents(), row.getCasinoBonusTransferFromBonusAmountCents(), rev));
				processedRecords++;
				reportRun.setProcessedRecords(processedRecords);
				reportRun = reportRunRepository.save(reportRun);
				
				Map<String, Boolean> filtration = new LinkedHashMap<String, Boolean>();
				int filterCount = 0;
				for (ReportFilter reportFilter: reportFilters) {
					filterCount++;
					if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_BIRTHDAY)) {
						if (row.getDateOfBirth() == null) { continue userLoop; }
						DateTime birthday = new DateTime(row.getDateOfBirth());
						birthday = birthday.withYear(new DateTime().getYear());
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), birthday.toDate()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_DEPOSIT_COUNT)) {
						if (row.getDepositCount() == null) { row.setDepositCount(0L);; }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getDepositCount()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_LAST_LOGIN_DATE)) {
						if (user.getLastLogin() == null) { continue userLoop; }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), user.getLastLogin().getDate()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_CASINO_BET_AMOUNT_CENTS)) {
						if (row.getCasinoBetAmountCents() == null) { row.setCasinoBetAmountCents(0L);; }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCasinoBetAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_CREATED_DATE)) {
						if (row.getCreatedDate() == null) { continue userLoop; }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getCreatedDate()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_DEPOSIT_AMOUNT_CENTS)) {
						if (row.getDepositAmountCents() == null) { row.setDepositAmountCents(0L); }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getDepositAmountCents()));
					} else if (reportFilter.getField().equalsIgnoreCase(ReportFilterService.FIELD_PLAYER_STATUS)) {
						if (row.getStatus() == null) { continue userLoop; }
						filtration.put(reportFilter.getField()+filterCount, reportFilterService.filter(reportFilter.getOperator(), reportFilter.getValue(), row.getStatus().getValue()));
					}
				}

				log.debug(filtration.toString());

				if (filtration.size() > 0) {
					boolean matchAllFilters = (rev.getAllFiltersApplicable() != null)? rev.getAllFiltersApplicable(): false;
					boolean matchedAtleastOneFilter = false;
					
					int countPlayerStatusFilter = 0;
					boolean matchedAtleastOnePlayerStatusFilter = false;
					
					filtrationLoop:
					for (Map.Entry<String, Boolean> entry: filtration.entrySet()) {
						if (entry.getKey().startsWith(ReportFilterService.FIELD_PLAYER_STATUS)) {
							countPlayerStatusFilter++;
							if (entry.getValue()) {
								matchedAtleastOnePlayerStatusFilter = true;
								matchedAtleastOneFilter = true;
							}
							continue filtrationLoop; 
						}
						if (matchAllFilters && !entry.getValue()) continue userLoop;
						if (entry.getValue()) matchedAtleastOneFilter = true;
					}
					if (countPlayerStatusFilter > 0) {
						if (!matchedAtleastOnePlayerStatusFilter) {
							continue userLoop;
						}
					}
					if (!matchedAtleastOneFilter) {
						continue userLoop;
					}
				}
				
				reportRunResultsRepository.save(row);
				
				filteredRecords++;
				reportRun.setFilteredRecords(filteredRecords);
				reportRun = reportRunRepository.save(reportRun);
			}
		}
		
		Thread.sleep(1000);
	}
	
	@Async
	public void run(Report report, String startedBy) {
		
		try {
		
			if (report.getRunning() != null) {
				log.debug("Report " + report + " already running");
				return;
			}
			
			ReportRun reportRun = ReportRun.builder()
					.reportRevision(report.getCurrent())
					.report(report)
					.startedBy(startedBy)
					.startedOn(new Date())
					.build();
			reportRun = reportRunRepository.save(reportRun);
			report.setRunning(reportRun);
			report = reportRepository.save(report);
			
			log.info("Report run started: " + reportRun);
			
			try {
				
				run(reportRun);
							
				// It might be some time after we got the last object, and we allow updates of the main report object
				// while reports run, so lets get the latest.
				report = reportRepository.findOne(report.getId());
				reportRun = reportRunRepository.findOne(reportRun.getId());
				
				reportActionService.processActions(reportRun);
				reportRun = reportRunRepository.findOne(reportRun.getId());
				
				reportRun.setTotalRecords(reportRun.getTotalRecords() == null? 0: reportRun.getTotalRecords());
				reportRun.setProcessedRecords(reportRun.getProcessedRecords() == null? 0: reportRun.getProcessedRecords());
				reportRun.setFilteredRecords(reportRun.getFilteredRecords() == null? 0: reportRun.getFilteredRecords());
				reportRun.setActionsPerformed(reportRun.getActionsPerformed() == null? 0: reportRun.getActionsPerformed());
				reportRun.setCompleted(true);
				reportRun.setCompletedOn(new Date());
				report.setLastCompleted(reportRun);
				report.setScheduledDate(null);
				reportRunRepository.save(reportRun);
				
				report.setRunRetriesCount(null);
				
				log.info("Report completed: " + reportRun);

			} catch (Exception ex) {
				log.error("Report run failed: " + reportRun + " " + ex, ex);
				reportRun = reportRunRepository.findOne(reportRun.getId());
				reportRun.setCompleted(true);
				reportRun.setCompletedOn(new Date());
				reportRun.setFailed(true);
				reportRun.setFailReason(ex.getMessage());
				report = reportRepository.findOne(report.getId());
				report.setLastFailed(reportRun);
				report.setRunRetriesCount((report.getRunRetriesCount() != null)? (report.getRunRetriesCount() + 1): 1);
				reportRunRepository.save(reportRun);
			}
			
			report.setRunning(null);
			reportRepository.save(report);
			
		} catch (Exception e) {
			log.error("Unhandled exception runnning report " + report + " " + e, e);
			
		}
		
	}

	private Long calculateNGRAmountCents(Long casinoNetAmountCents, Long casinoBonusTransferToBonusAmountCents, Long casinoBonusTransferFromBonusAmountCents, ReportRevision rev) {
		// FIXME: 2019/07/01 Produce the true NGR calculation. This is only a rough version of ngr. There is a better way to do it to avoid affiliates exploiting bonus uptake and completion.
		BigDecimal ggrDeductionPercentage = new BigDecimal(rev.getGgrPercentageDeduction());
		ggrDeductionPercentage = ggrDeductionPercentage.movePointLeft(2); //Turn it into a multiplication operand participant

		BigDecimal ngrRaw = new BigDecimal(zeroOrValue(casinoNetAmountCents) + zeroOrValue(casinoBonusTransferToBonusAmountCents) - zeroOrValue(casinoBonusTransferFromBonusAmountCents));

		BigDecimal ngrModified = ngrRaw.subtract(ngrRaw.multiply(ggrDeductionPercentage));
		return ngrModified.longValue(); //Chop off the decimal portion.
	}

	/**
	 * Attempt at generics but with java, zero is not just zero. This will only work for long types.
	 * @param number
	 * @param <E>
	 * @return
	 */
	static public <E extends Number> E zeroOrValue(E number) {
		if (number == null) return (E)(Number)0L;

		return number;
	}
}
