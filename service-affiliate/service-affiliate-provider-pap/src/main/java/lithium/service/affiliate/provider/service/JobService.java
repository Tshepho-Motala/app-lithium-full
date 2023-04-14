package lithium.service.affiliate.provider.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainTransactionTypeClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.accounting.objects.SummaryTransactionType;
import lithium.service.affiliate.provider.ServiceAffiliateProviderPapModuleInfo;
import lithium.service.affiliate.provider.ServiceAffiliateProviderPapModuleInfo.ConfigProperties;
import lithium.service.affiliate.provider.data.entities.BatchRun;
import lithium.service.affiliate.provider.stream.ExportStream;
import lithium.service.affiliate.provider.stream.objects.PapTransactionStreamData;
import lithium.service.cashier.CashierTransactionLabels;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.casino.CasinoTranType;
import lithium.service.casino.client.CasinoBonusClient;
import lithium.service.casino.client.data.PlayerBonusHistory;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.ProviderClient;
import lithium.service.domain.client.objects.Provider;
import lithium.service.domain.client.objects.ProviderProperty;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.UserSignupClient;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JobService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired ServiceAffiliateProviderPapModuleInfo info;
	@Autowired BatchRunService batchRunService;
	@Autowired ExportStream exportStream;
	
	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Async
	public void runDataExportForDomain(Provider provider) {
		LocalDateTime today = LocalDateTime.now();
		today = today.minusHours(today.getHour()).minusMinutes(today.getMinute()).minusSeconds(today.getSecond()).minusNanos(today.getNano());
		LocalDateTime yesterday = today.minusDays(1);
		
		final Date dateEnd = Date.from(today.toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())));
		final Date dateStart = Date.from(yesterday.toInstant(ZoneId.systemDefault().getRules().getOffset(Instant.now())));
		
		boolean executedBefore = checkForPreviousExecution(provider.getDomain().getName(), 
				Period.GRANULARITY_DAY, 
				provider.getDomain().getCurrency(), 
				dateStart,
				dateEnd);
		
		if (executedBefore) return;
		
		boolean dataReady = isDataReadyForExport(provider.getDomain().getName(), Period.GRANULARITY_DAY, provider.getDomain().getCurrency(), dateStart, dateEnd);
		
		if (!dataReady) return;
		
		runExport(provider.getDomain().getName(), Period.GRANULARITY_DAY, provider.getDomain().getCurrency(), dateStart, dateEnd);
	}
	
	public void runExport(String domainName, int granularity, String currency, Date dateStart, Date dateEnd) {
		
		final BatchRun batchRun = startBatchRun(domainName, granularity, currency, dateStart, dateEnd);
		
		List<User> userList = getUserApiClient().get().usersByDomainAndLabel(domainName, Label.AFFILIATE_GUID_LABEL);
		
		ProviderClient providerClient = getProviderClient().get(); //TODO: Find way around possible exception if domain service is not available
		
		//DomainClient domainClient = getDomainClient().get();
		
		//Response<String> setting = domainClient.getSetting(domainName, DomainSettings.AFFILIATE_EXPORT_NO_BONUS_TRANS.getName());
		

		HashMap<String,String> providerProperties= getProviderProperties(providerClient, domainName, info.getModuleName());
		
		boolean exportNoBonusTrans = false;
		
		if (providerProperties.get(ConfigProperties.EXPORT_NO_BONUS_TRANS.getValue()) != null && providerProperties.get(ConfigProperties.EXPORT_NO_BONUS_TRANS.getValue()).contentEquals("true")) {
			exportNoBonusTrans = true;
		}
		
		final boolean enbt = exportNoBonusTrans;
			userList.forEach((user) -> { 
				try {
					boolean success = false;
						if (enbt) {
							success = getAccountingDataAndExportNoBonusTrans(user, domainName, granularity, currency, dateStart, dateEnd, 
									providerProperties.get(ConfigProperties.BASE_URL.getValue()), providerProperties.get(ConfigProperties.AUTH_TOKEN_USER.getValue()), 
									providerProperties.get(ConfigProperties.AUTH_TOKEN_PASSWORD.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_BET.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_WIN.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_BONUS.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_FIRST_DEPOSIT.getValue()),
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_DEPOSIT.getValue()),
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_SIGNUP.getValue()),
									providerProperties.get(ConfigProperties.REFERRER_URL.getValue()));
						} else {
							success = getAccountingDataAndExport(user, domainName, granularity, currency, dateStart, dateEnd, 
									providerProperties.get(ConfigProperties.BASE_URL.getValue()), providerProperties.get(ConfigProperties.AUTH_TOKEN_USER.getValue()), 
									providerProperties.get(ConfigProperties.AUTH_TOKEN_PASSWORD.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_BET.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_WIN.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_BONUS.getValue()), 
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_FIRST_DEPOSIT.getValue()),
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_DEPOSIT.getValue()),
									providerProperties.get(ConfigProperties.COMMISSION_TYPE_ID_SIGNUP.getValue()),
									providerProperties.get(ConfigProperties.REFERRER_URL.getValue()));
						}
					batchRunService.update(batchRun.getId());
					//TODO: update batch run + log failure/success
				} catch (Exception ex) {
					log.warn("Problem performing PaP data export, will be retried on next schedule execution", ex);
					return;
				}
			});
		
		batchRunService.finish(batchRun.getId());
	}

	private HashMap<String, String> getProviderProperties(ProviderClient providerClient, String domainName,
			String serviceUrl) {
		HashMap<String, String> props = new HashMap<>();
		Response<Iterable<ProviderProperty>> propListResponse = providerClient.propertiesByProviderUrlAndDomainName(serviceUrl, domainName);
		
		if (propListResponse.getStatus() == Status.OK) {
			propListResponse.getData().forEach( property -> { props.put(property.getName(), property.getValue()); } ); 
			return props;
		}
		
		return null;
	}

	//Check if there was a run for the day before
	public boolean checkForPreviousExecution(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		BatchRun batchRun = batchRunService.find(domain, granularity, currency, dateStart, dateEnd);
		
		if (batchRun == null) return false;
		
		return true;
	}
	
	//Check accounting system for consistency in transactions and transaction enrichment
	public boolean isDataReadyForExport(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		
		LocalDateTime dateEndLocal = LocalDateTime.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault());
		
		long totalDomainTrans = getTotalTransForDomain(domain, granularity, currency, dateStart, dateEnd);
		long totalDomainTransLv = getTotalTransForDomainLabelValue(domain, granularity, currency, dateStart, dateEnd, Label.AFFILIATED_LABEL, "yes");
		totalDomainTransLv += getTotalTransForDomainLabelValue(domain, granularity, currency, dateStart, dateEnd, Label.AFFILIATED_LABEL, "no");
		
		if (Period.GRANULARITY_DAY == granularity) {
			LocalDateTime dt = LocalDateTime.now();
			dt = dt.minusMinutes(45); // Allow 45 minutes past the day before execution is allowed
			
			if (dt.isBefore(dateEndLocal)) {
				log.info("The end date is not in a completed period, there could still be transactions");
				return false;
			}
			
		} else {
			log.error("Only catering for DAY granylarity for now. Not going to compare trans");
			return false;
		}
		
		if (totalDomainTrans == totalDomainTransLv) {
			log.info("PapTransactionStreamData: " + domain + " can be exported with granularity: "+ " and currency: " + currency + " for date range: " + dateStart + " - " + dateEnd);
			return true;
		} else {
			log.warn("The summary accounting and summary domain trans have a mismatch, this is a known problem and will be fixed when damage on summary is reworked. total_domain_trans: " + totalDomainTrans + " total_lv_trans: " + totalDomainTransLv);
			//FIXME: This needs to change once we have the damage for summary trans reworked
			return true;
		}
		
		//return false;
	}
	
	private long getTotalTransForDomain(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		Response<List<SummaryTransactionType>> resultTotalBet = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBetRollback = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalWin = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
//		Response<List<SummaryTransactionType>> resultTotalWinFreespin = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalWinRollback = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalNegBet = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalDeposit = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		
		Response<List<SummaryTransactionType>> resultTotalBonusBet = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusBetRollback = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusWin = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusWinFreespin = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusWinRollback = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusNegBet = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		
		Response<List<SummaryTransactionType>> resultTotalBonusCancelled = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_CANCEL.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusExpired = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_EXPIRED.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), currency, formatDate(dateStart), formatDate(dateEnd));
		Response<List<SummaryTransactionType>> resultTotalBonusExcess = getAccountingSummaryDomainTransactionTypeClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), currency, formatDate(dateStart), formatDate(dateEnd));

		long grandTotalDomainTrans = 0L;
		
		grandTotalDomainTrans += getTotalFromResult(resultTotalBet);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBetRollback);
		grandTotalDomainTrans += getTotalFromResult(resultTotalWin);
//		grandTotalDomainTrans += getTotalFromResult(resultTotalWinFreespin);
		grandTotalDomainTrans += getTotalFromResult(resultTotalWinRollback);
		grandTotalDomainTrans += getTotalFromResult(resultTotalNegBet);
		grandTotalDomainTrans += getTotalFromResult(resultTotalDeposit);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusBet);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusBetRollback);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusWin);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusWinFreespin);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusWinRollback);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusNegBet);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusCancelled);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusExpired);
		grandTotalDomainTrans += getTotalFromResult(resultTotalBonusExcess);
		
		return grandTotalDomainTrans;
	}
	
	private long getTotalTransForDomainLabelValue(String domain, int granularity, String currency, Date dateStart, Date dateEnd, String labelName, String labelValue) {
		Response<List<SummaryLabelValue>> resultTotalBet = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBetRollback = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalWin = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
//		Response<List<SummaryLabelValue>> resultTotalWinFreespin = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalWinRollback = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalNegBet = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalDeposit = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		
		Response<List<SummaryLabelValue>> resultTotalBonusBet = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusBetRollback = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusWin = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusWinFreespin = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusWinRollback = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusNegBet = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		
		Response<List<SummaryLabelValue>> resultTotalBonusCancelled = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_CANCEL.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusExpired = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_EXPIRED.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalBonusExcess = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		
		Response<List<SummaryLabelValue>> resultTotalManualBonusFreeFunds = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, "MANUAL_BONUS_FREE_FUNDS", "BALANCE_ADJUST", labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalChargebacks = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, "CHARGEBACK", "BALANCE_ADJUST", labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());
		Response<List<SummaryLabelValue>> resultTotalManualCasinoBonus = getAccountingSummaryDomainLabelValueClient().get().findLimited(domain, granularity, "MANUAL_BONUS_CASINO", "BALANCE_ADJUST", labelName, labelValue, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString());

		long grandTotalDomainTrans = 0L;
		
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBet);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBetRollback);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalWin);
//		grandTotalDomainTrans += getTotalFromResultLv(resultTotalWinFreespin);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalWinRollback);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalNegBet);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalDeposit);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusBet);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusBetRollback);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusWin);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusWinFreespin);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusWinRollback);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusNegBet);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusCancelled);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusExpired);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalBonusExcess);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalManualBonusFreeFunds);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalChargebacks);
		grandTotalDomainTrans += getTotalFromResultLv(resultTotalManualCasinoBonus);
		
		return grandTotalDomainTrans;
	}
	
	private long getTotalFromResult(Response<List<SummaryTransactionType>> resultList) {
		Long totalTrans = 0L;
		
		if (resultList.getStatus() == Status.OK) {
			for (SummaryTransactionType t : resultList.getData()) {
				totalTrans += t.getTranCount();
			}
		}
		
		return totalTrans;
	}
	
	private long getTotalFromResultLv(Response<List<SummaryLabelValue>> resultList) {
		Long totalTrans = 0L;
		
		if (resultList.getStatus() == Status.OK) {
			for (SummaryLabelValue t : resultList.getData()) {
				totalTrans += t.getTranCount();
			}
		}
		
		return totalTrans;
	}
	
	private BatchRun startBatchRun(String domain, int granularity, String currency, Date dateStart, Date dateEnd) {
		BatchRun batchRun = batchRunService.create(domain, granularity, currency, dateStart, dateEnd);
		return batchRun;
	}
	
	private boolean getAccountingDataAndExport(User user, String domain, int granularity, String currency, Date dateStart, Date dateEnd, String baseUrl, String authTokenUser, String authTokenPassword, String commTypeIdBet, String commTypeIdWin, String commTypeIdBonus, String commTypeIdFirstDeposit, String commTypeIdDeposit, String commTypeIdSignup, String referrerUrl) throws Exception {
		String affiliateGuid = user.getLabelAndValue().get(Label.AFFILIATE_GUID_LABEL);
		String bannerGuid = user.getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_1_LABEL);
		String campaignGuid = user.getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_2_LABEL);
		String userGuid = domain+"/"+user.getUsername();
		Response<List<SummaryLabelValue>> resultTotalBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBetRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalWin = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
//		Response<List<SummaryLabelValue>> resultTotalWinFreespin = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalWinRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalNegBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalBonusBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusBetRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusWin = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusWinFreespin = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_FREESPIN.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusWinRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusNegBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYER_BALANCE_CASINO_BONUS.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);

		Response<List<SummaryLabelValue>> resultTotalFirstTimeDeposit = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), CashierTransactionLabels.FIRST_DEPOSIT_LABEL.toString(), "yes", currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalNonFirstTimeDeposit = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), CashierTransactionLabels.FIRST_DEPOSIT_LABEL.toString(), "no", currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		List<SummaryLabelValue> bonusAllocationList = new ArrayList<>();
		//Bonus amount written as credit to counter the bonus bet and win
		List<PlayerBonusHistory> bonusHistoryList = getCasinoBonusClient().get().findBonusHistoryByDateRange(userGuid, (new DateTime(dateStart)).toString(), (new DateTime(dateEnd)).toString());
		bonusHistoryList.forEach(bonusHistory -> { 
			bonusAllocationList.add(SummaryLabelValue.builder()
					.creditCents(bonusHistory.getBonusAmount())
					.debitCents(0L)
					.dateStart(bonusHistory.getStartedDate())
					.dateEnd(dateEnd)
					.build()); 
			} );
		Response<List<SummaryLabelValue>> bonusAllocationResponse = Response.<List<SummaryLabelValue>>builder().status(Status.OK).data(bonusAllocationList).build();
		Response<List<SummaryLabelValue>> resultTotalBonusCancelled = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_CANCEL.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusExpired = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_EXPIRED.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBonusExcess = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalManualBonusFreeFunds = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, "MANUAL_BONUS_FREE_FUNDS", "BALANCE_ADJUST", Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalChargebacks = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, "CHARGEBACK", "BALANCE_ADJUST", Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalManualCasinoBonus = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, "MANUAL_BONUS_CASINO", "BALANCE_ADJUST", Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		//Write signup entry
		if (user.getCreatedDate().after(dateStart) && user.getCreatedDate().before(dateEnd)) {
			List<SummaryLabelValue> signupList = new ArrayList<>();
			
			signupList.add(SummaryLabelValue.builder()
			.creditCents(0L)
			.debitCents(0L)
			.dateStart(user.getCreatedDate())
			.dateEnd(dateEnd)
			.build()); 
			
			Response<List<SummaryLabelValue>> signupResponse = Response.<List<SummaryLabelValue>>builder().status(Status.OK).data(signupList).build();
			
			queueData(signupResponse, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "SIGNUP", baseUrl, authTokenUser, authTokenPassword, commTypeIdSignup, referrerUrl, false);
		}
		
		return (queueData(resultTotalManualBonusFreeFunds, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "MANUAL_BONUS_FREE_FUNDS", baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalChargebacks, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "CHARGEBACK", baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalManualCasinoBonus, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "MANUAL_BONUS_CASINO", baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalBonusBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalBonusBetRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalBonusWin, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalBonusWinFreespin, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN_FREESPIN.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalBonusWinRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalBonusNegBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_NEGATIVE_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalBetRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalWin, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
//				queueData(resultTotalWinFreespin, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN_FREESPIN.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalWinRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalNegBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_NEGATIVE_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(bonusAllocationResponse, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BONUS_ACTIVATE.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl) &&
				queueData(resultTotalBonusCancelled, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BONUS_CANCEL.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalBonusExpired, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BONUS_EXPIRED.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalBonusExcess, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BONUS_MAXPAYOUT_EXCESS.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalFirstTimeDeposit, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CashierTranType.DEPOSIT.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdFirstDeposit, referrerUrl, true) &&
				queueData(resultTotalNonFirstTimeDeposit, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CashierTranType.DEPOSIT.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdDeposit, referrerUrl, true));

	}

	private boolean getAccountingDataAndExportNoBonusTrans(User user, String domain, int granularity, String currency, Date dateStart, Date dateEnd, String baseUrl, String authTokenUser, String authTokenPassword, String commTypeIdBet, String commTypeIdWin, String commTypeIdBonus, String commTypeIdFirstDeposit, String commTypeIdDeposit, String commTypeIdSignup, String referrerUrl) throws Exception {
		String affiliateGuid = user.getLabelAndValue().get(Label.AFFILIATE_GUID_LABEL);
		String bannerGuid = user.getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_1_LABEL);
		String campaignGuid = user.getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_2_LABEL);
		String userGuid = domain+"/"+user.getUsername();
		Response<List<SummaryLabelValue>> resultTotalBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalBetRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_BET_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalWin = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalWinRollback = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_WIN_ROLLBACK.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		Response<List<SummaryLabelValue>> resultTotalNegBet = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.CASINO_NEGATIVE_BET.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalFirstTimeDeposit = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), CashierTransactionLabels.FIRST_DEPOSIT_LABEL.toString(), "yes", currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalNonFirstTimeDeposit = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CashierTranType.DEPOSIT.toString(), CashierTransactionLabels.FIRST_DEPOSIT_LABEL.toString(), "no", currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalBonusToPlayerBalance = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, CasinoTranType.PLAYERBALANCE.toString(), CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		Response<List<SummaryLabelValue>> resultTotalChargebacks = getAccountingSummaryAccountLabelValueClient().get().findLimited(domain, granularity, "CHARGEBACK", "BALANCE_ADJUST", Label.AFFILIATE_GUID_LABEL, affiliateGuid, currency, dateStart.toInstant().toString(), dateEnd.toInstant().toString(), userGuid);
		
		//Trigger amounts for bonus are written as debit to affiliate system, so affiliate gets paid. The transfer from bonus transaction is to counter this should the player win something off bonus.
		List<SummaryLabelValue> bonusAllocationList = new ArrayList<>();
		List<PlayerBonusHistory> bonusHistoryList = getCasinoBonusClient().get().findBonusHistoryByDateRange(userGuid, (new DateTime(dateStart)).toString(), (new DateTime(dateEnd)).toString());
		bonusHistoryList.forEach(bonusHistory -> { 
			bonusAllocationList.add(SummaryLabelValue.builder()
					.creditCents(0L)
					.debitCents(bonusHistory.getTriggerAmount())
					.dateStart(bonusHistory.getStartedDate())
					.dateEnd(dateEnd)
					.build()); 
			} );
		Response<List<SummaryLabelValue>> bonusAllocationResponse = Response.<List<SummaryLabelValue>>builder().status(Status.OK).data(bonusAllocationList).build();
		//Write signup entry
		if (user.getCreatedDate().after(dateStart) && user.getCreatedDate().before(dateEnd)) {
			List<SummaryLabelValue> signupList = new ArrayList<>();
			
			signupList.add(SummaryLabelValue.builder()
			.creditCents(0L)
			.debitCents(0L)
			.dateStart(user.getCreatedDate())
			.dateEnd(dateEnd)
			.build()); 
			
			Response<List<SummaryLabelValue>> signupResponse = Response.<List<SummaryLabelValue>>builder().status(Status.OK).data(signupList).build();
			
			queueData(signupResponse, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "SIGNUP", baseUrl, authTokenUser, authTokenPassword, commTypeIdSignup, referrerUrl, false);
		}
		
		return (queueData(resultTotalChargebacks, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, "CHARGEBACK", baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl, true) &&
				queueData(resultTotalBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalBetRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_BET_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(resultTotalWin, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalWinRollback, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_WIN_ROLLBACK.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdWin, referrerUrl) &&
				queueData(resultTotalNegBet, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.CASINO_NEGATIVE_BET.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBet, referrerUrl) &&
				queueData(bonusAllocationResponse, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.TRANSFER_TO_CASINO_BONUS.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl) &&
				queueData(resultTotalBonusToPlayerBalance, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CasinoTranType.TRANSFER_FROM_CASINO_BONUS.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdBonus, referrerUrl) &&
				queueData(resultTotalFirstTimeDeposit, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CashierTranType.DEPOSIT.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdFirstDeposit, referrerUrl, true) &&
				queueData(resultTotalNonFirstTimeDeposit, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, CashierTranType.DEPOSIT.toString(), baseUrl, authTokenUser, authTokenPassword, commTypeIdDeposit, referrerUrl, true));

	}

	private boolean queueData(Response<List<SummaryLabelValue>> resultList, String userGuid, String domain, String affiliateGuid, int granularity, String currency, Date dateStart, Date dateEnd, String bannerGuid, String campaignGuid, String transactionType,
			String baseUrl, String authTokenUser, String authTokenPassword, String commTypeId, String referrerUrl) {
		return queueData(resultList, userGuid, domain, affiliateGuid, granularity, currency, dateStart, dateEnd, bannerGuid, campaignGuid, transactionType, baseUrl, authTokenUser, authTokenPassword, commTypeId, referrerUrl, false);
	}
	
	private boolean queueData(Response<List<SummaryLabelValue>> resultList, String userGuid, String domain, String affiliateGuid, int granularity, String currency, Date dateStart, Date dateEnd, String bannerGuid, String campaignGuid, String transactionType,
			String baseUrl, String authTokenUser, String authTokenPassword, String commTypeId, String referrerUrl, boolean invertAmountSignage) {
		
		if (resultList.getStatus() == Status.OK) {
			for (SummaryLabelValue t : resultList.getData()) {
				BigDecimal amount = new BigDecimal(t.getCreditCents() > 0 ? t.getCreditCents()*-1L : t.getDebitCents());
				amount = amount.movePointLeft(2);
				if (invertAmountSignage) {
					amount = amount.multiply(new BigDecimal(-1));
				}
				exportStream.register(PapTransactionStreamData.builder()
						.ownerGuid(userGuid)
						.affiliateGuid(affiliateGuid)
						.amount(amount.toString())
						.bannerGuid(bannerGuid)
						.transactionType(transactionType)
						.transactionDate(t.getDateStart())
						.campaignGuid(campaignGuid)
						.baseUrl(baseUrl)
						.authTokenUser(authTokenUser)
						.authTokenPassword(authTokenPassword)
						.commissionTypeId(commTypeId)
						.referrerUrl(referrerUrl)
						.build());
			}
		} else {
			log.error("Problem with result list export: " + resultList.getStatus());
			return false;
		}
		
		return true;
	}

	public Optional<UserApiClient> getUserApiClient() {
		return getClient(UserApiClient.class, "service-user");
	}
	
	public Optional<AccountingClient> getAccountingClient() {
		return getClient(AccountingClient.class, "service-accounting");
	}

	public Optional<AccountingSummaryDomainLabelValueClient> getAccountingSummaryDomainLabelValueClient() {
		return getClient(AccountingSummaryDomainLabelValueClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<AccountingSummaryDomainTransactionTypeClient> getAccountingSummaryDomainTransactionTypeClient() {
		return getClient(AccountingSummaryDomainTransactionTypeClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<AccountingSummaryAccountLabelValueClient> getAccountingSummaryAccountLabelValueClient() {
		return getClient(AccountingSummaryAccountLabelValueClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<CasinoBonusClient> getCasinoBonusClient() {
		return getClient(CasinoBonusClient.class, "service-casino");
	}
	
	public Optional<UserSignupClient> getUserSignupClient() {
		return getClient(UserSignupClient.class, "service-user");
	}
	
	public Optional<ProviderClient> getProviderClient() {
		return getClient(ProviderClient.class, "service-domain");
	}
	
	public Optional<DomainClient> getDomainClient() {
		return getClient(DomainClient.class, "service-domain");
	}
	
	public <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
		
	}
	
	private String formatDate(final Date d) {
		return df.format(d);
	}
}