package lithium.service.cashier.services;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingSummaryAccountLabelValueClient;
import lithium.service.accounting.client.AccountingSummaryDomainLabelValueClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryLabelValue;
import lithium.service.cashier.CashierTransactionLabels;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.DomainMethodProcessorProfile;
import lithium.service.cashier.data.entities.DomainMethodProcessorUser;
import lithium.service.cashier.data.entities.Limits;
import lithium.service.cashier.data.entities.User;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CashierAccountingChecksService {
	@Autowired
	private DomainMethodProcessorProfileService domainMethodProcessorProfileService;
	@Autowired
	private DomainMethodProcessorUserService domainMethodProcessorUserService;
	@Autowired
	private UserService userService;
	@Autowired
	private LithiumServiceClientFactory services;
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		
		try {
			clientInstance = services.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
		
	}
	private Optional<AccountingSummaryAccountLabelValueClient> getAccountingSummaryAccountLabelValueClient() {
		return getClient(AccountingSummaryAccountLabelValueClient.class, "service-accounting-provider-internal");
	}
	private Optional<AccountingSummaryDomainLabelValueClient> getAccountingSummaryDomainLabelValueClient() {
		return getClient(AccountingSummaryDomainLabelValueClient.class, "service-accounting-provider-internal");
	}
	
	private SummaryLabelValue getAccountingSummary(
		boolean isDeposit,
		String domainName,
		Long dmpId,
		int granularity
	) throws Exception {
		return getAccountingSummary(isDeposit, domainName, dmpId, granularity, null);
	}
	
	private SummaryLabelValue getAccountingSummary(
		boolean isDeposit,
		String domainName,
		Long dmpId,
		int granularity,
		String playerGuid
	) throws Exception {
		String currency = userService.retrieveDomainFromDomainService(domainName).getCurrency();
		String accountCode = (isDeposit)?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		String transactionType = (isDeposit)?CashierTranType.DEPOSIT.value():CashierTranType.PAYOUT.value();
		
		DateTimeZone timeZone = DateTimeZone.getDefault();
		DateTime now = DateTime.now(timeZone);
		DateTime start = now;
		DateTime end = now;
		switch (granularity) {
			case Period.GRANULARITY_MONTH:
				start = now.withTimeAtStartOfDay().dayOfMonth().withMinimumValue();
				end = now.withTimeAtStartOfDay().dayOfMonth().withMaximumValue().plusDays(1);
				break;
			case Period.GRANULARITY_WEEK:
				start = now.withTimeAtStartOfDay().dayOfWeek().withMinimumValue();
				end = now.withTimeAtStartOfDay().dayOfWeek().withMaximumValue().plusDays(1);
				break;
			case Period.GRANULARITY_DAY:
				start = now.withTimeAtStartOfDay();
				end = now.plusDays(1).withTimeAtStartOfDay();
				break;
			default:
				break;
		}
		
		Response<List<SummaryLabelValue>> accountingSummary = null;
		
		if (playerGuid!=null) {
			accountingSummary = getAccountingSummaryAccountLabelValueClient().get().findLimited(
				domainName,
				granularity,
				accountCode,
				transactionType,
				CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
				dmpId+"",
				currency,
				start.toString(),
				end.toString(),
				playerGuid
			);
		} else {
			accountingSummary = getAccountingSummaryDomainLabelValueClient().get().findLimited(
				domainName,
				granularity,
				accountCode,
				transactionType,
				CashierTransactionLabels.DOMAIN_METHOD_PROCESSOR_ID,
				dmpId+"",
				currency,
				start.toString(),
				end.toString()
			);
		}
		if (accountingSummary.isSuccessful() && accountingSummary.getData().size() > 0) {
			return accountingSummary.getData().get(0);
		}
		return null;
	}
	
	public boolean accountingChecks(DomainMethodProcessor dmp, User user) throws Exception {
		boolean isDeposit = dmp.getDomainMethod().getDeposit();
		
		DomainMethodProcessorUser dmpu = domainMethodProcessorUserService.findByDomainMethodProcessorAndUser(dmp, user);
		Limits ipl = (dmpu!=null)?dmpu.getLimits():null; //individual player limits (IPL)
		DomainMethodProcessorProfile dmpp = domainMethodProcessorProfileService.findByDomainMethodProcessorAndProfile(dmp, user.getProfile());
		Limits pl = (dmpp!=null)?dmpp.getLimits():null; //profile limits (PL)
		Limits gpl = dmp.getLimits(); //global player limits (GPL)
		Limits dl = dmp.getDomainLimits(); //domain limits (DL)
		
		long domainMonthTranCount = 0L;
		long domainWeekTranCount = 0L;
		long domainDayTranCount = 0L;
		long domainMonthAmountCents = 0L;
		long domainWeekAmountCents = 0L;
		long domainDayAmountCents = 0L;
		
		long userMonthTranCount = 0L;
		long userWeekTranCount = 0L;
		long userDayTranCount = 0L;
		long userMonthAmountCents = 0L;
		long userWeekAmountCents = 0L;
		long userDayAmountCents = 0L;
		
		log.debug("==============================================================");
		log.debug("======== "+dmp.getDomainMethod().getName()+" ========");
		log.debug("==============================================================");
		String logMsg = "("+dmp.getDescription()+")(deposit:"+isDeposit+") :: ";
		
		SummaryLabelValue userMonth = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_MONTH, user.guid());
		if (userMonth!=null) {
			userMonthTranCount = userMonth.getTranCount();
			userMonthAmountCents = (isDeposit)?userMonth.getDebitCents():userMonth.getCreditCents();
		}
		logMsg += " userMonthTranCount:"+userMonthTranCount+" userMonthAmountCents:"+userMonthAmountCents;
		SummaryLabelValue userWeek = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_WEEK, user.guid());
		if (userWeek!=null) {
			userWeekTranCount = userWeek.getTranCount();
			userWeekAmountCents = (isDeposit)?userWeek.getDebitCents():userWeek.getCreditCents();
		}
		logMsg += " userWeekTranCount:"+userWeekTranCount+" userWeekAmountCents:"+userWeekAmountCents;
		SummaryLabelValue userDay = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_DAY, user.guid());
		if (userDay!=null) {
			userDayTranCount = userDay.getTranCount();
			userDayAmountCents = (isDeposit)?userDay.getDebitCents():userDay.getCreditCents();
		}
		logMsg += " userDayTranCount:"+userDayTranCount+" userDayAmountCents:"+userDayAmountCents;
		log.debug(logMsg);
		logMsg = "("+dmp.getDescription()+")(deposit:"+isDeposit+") :: ";
		
		SummaryLabelValue domainMonth = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_MONTH);
		if (domainMonth!=null) {
			domainMonthTranCount = domainMonth.getTranCount();
			domainMonthAmountCents = (isDeposit)?domainMonth.getDebitCents():domainMonth.getCreditCents();
		}
		logMsg += " domainMonthTranCount:"+domainMonthTranCount+" domainMonthAmountCents:"+domainMonthAmountCents;
		SummaryLabelValue domainWeek = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_WEEK);
		if (domainWeek!=null) {
			domainWeekTranCount = domainWeek.getTranCount();
			domainWeekAmountCents = (isDeposit)?domainWeek.getDebitCents():domainWeek.getCreditCents();
		}
		logMsg += " domainWeekTranCount:"+domainWeekTranCount+" domainWeekAmountCents:"+domainWeekAmountCents;
		SummaryLabelValue domainDay = getAccountingSummary(isDeposit, user.domainName(), dmp.getId(), Period.GRANULARITY_DAY);
		if (domainDay!=null) {
			domainDayTranCount = domainDay.getTranCount();
			domainDayAmountCents = (isDeposit)?domainDay.getDebitCents():domainDay.getCreditCents();
		}
		logMsg += " domainDayTranCount:"+domainDayTranCount+" domainDayAmountCents:"+domainDayAmountCents;
		log.debug(logMsg);
		
		Long domainTranLimitMonth =  dl.getMaxTransactionsMonth();
		Long domainTranLimitWeek =  dl.getMaxTransactionsWeek();
		Long domainTranLimitDay =  dl.getMaxTransactionsDay();
		Long domainAmountLimitMonth = dl.getMaxAmountMonth();
		Long domainAmountLimitWeek = dl.getMaxAmountWeek();
		Long domainAmountLimitDay = dl.getMaxAmountDay();
		
		//First check domain level limits
		if ((domainMonthTranCount < domainTranLimitMonth) && (domainWeekTranCount < domainTranLimitWeek) && (domainDayTranCount < domainTranLimitDay)) {
			if ((domainMonthAmountCents < domainAmountLimitMonth) && (domainWeekAmountCents < domainAmountLimitWeek) && (domainDayAmountCents < domainAmountLimitDay)) {
				//Made it past domain level checks, still good to go, lets check individual player limits (IPL) ..
				boolean userMonthAmountEmpty = true;
				boolean userWeekAmountEmpty = true;
				boolean userDayAmountEmpty = true;
				boolean userMonthTranCountEmpty = true;
				boolean userWeekTranCountEmpty = true;
				boolean userDayTranCountEmpty = true;
				logMsg = "individual player limits (IPL) check : ";
				if ((ipl!=null) && (ipl.getMaxTransactionsMonth()!=null)) userMonthTranCountEmpty = false;
				if ((!userMonthTranCountEmpty) && (userMonthTranCount >= ipl.getMaxTransactionsMonth())) {
					logMsg += "transaction limit reached (month) :: limit: "+ipl.getMaxTransactionsMonth()+" player transaction count (month) : "+userMonthTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((ipl!=null) && (ipl.getMaxTransactionsWeek()!=null)) userWeekTranCountEmpty = false;
				if ((!userWeekTranCountEmpty) && (userWeekTranCount >= ipl.getMaxTransactionsWeek())) {
					logMsg += "transaction limit reached (week) :: limit: "+ipl.getMaxTransactionsWeek()+" player transaction count (week) : "+userWeekTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((ipl!=null) && (ipl.getMaxTransactionsDay()!=null)) userDayTranCountEmpty = false;
				if ((!userDayTranCountEmpty) && (userDayTranCount >= ipl.getMaxTransactionsDay())) {
					logMsg += "transaction limit reached (day) :: limit: "+ipl.getMaxTransactionsDay()+" player transaction count (day) : "+userDayTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((ipl!=null) && (ipl.getMaxAmountMonth()!=null)) userMonthAmountEmpty = false;
				if ((!userMonthAmountEmpty) && (userMonthAmountCents >= ipl.getMaxAmountMonth())) {
					logMsg += "amount limit reached (month) :: limit: "+ipl.getMaxAmountMonth()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (month) : "+userMonthAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((ipl!=null) && (ipl.getMaxAmountWeek()!=null)) userWeekAmountEmpty = false;
				if ((!userWeekAmountEmpty) && (userWeekAmountCents >= ipl.getMaxAmountWeek())) {
					logMsg += "amount limit reached (week) :: limit: "+ipl.getMaxAmountWeek()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (week) : "+userWeekAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((ipl!=null) && (ipl.getMaxAmountDay()!=null)) userDayAmountEmpty = false;
				if ((!userDayAmountEmpty) && (userDayAmountCents >= ipl.getMaxAmountDay())) {
					logMsg += "amount limit reached (day) :: limit: "+ipl.getMaxAmountDay()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (day) : "+userDayAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				logMsg += "passed!";
				log.debug(logMsg);
				
				boolean profileMonthAmountEmpty = true;
				boolean profileWeekAmountEmpty = true;
				boolean profileDayAmountEmpty = true;
				boolean profileMonthTranCountEmpty = true;
				boolean profileWeekTranCountEmpty = true;
				boolean profileDayTranCountEmpty = true;
				//Made it past individual player limits (IPL) .. Checking profile limits (PL) (if user level not set)
				logMsg = "profile limits (PL) check : ";
				if ((pl!=null) && (pl.getMaxTransactionsMonth()!=null)) profileMonthTranCountEmpty = false;
				if ((userMonthTranCountEmpty) && (!profileMonthTranCountEmpty) && (userMonthTranCount >= pl.getMaxTransactionsMonth())) {
					logMsg += "transaction limit reached (month) :: limit: "+pl.getMaxTransactionsMonth()+" player transaction count (month) : "+userMonthTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((pl!=null) && (pl.getMaxTransactionsWeek()!=null)) profileWeekTranCountEmpty = false;
				if ((userWeekTranCountEmpty) && (!profileWeekTranCountEmpty) && (userWeekTranCount >= pl.getMaxTransactionsWeek())) {
					logMsg += "transaction limit reached (week) :: limit: "+pl.getMaxTransactionsWeek()+" player transaction count (week) : "+userWeekTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((pl!=null) && (pl.getMaxTransactionsDay()!=null)) profileDayTranCountEmpty = false;
				if ((userDayTranCountEmpty) && (!profileDayTranCountEmpty) && (userDayTranCount >= pl.getMaxTransactionsDay())) {
					logMsg += "transaction limit reached (day) :: limit: "+pl.getMaxTransactionsDay()+" player transaction count (day) : "+userDayTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((pl!=null) && (pl.getMaxAmountMonth()!=null)) profileMonthAmountEmpty = false;
				if ((userMonthAmountEmpty) && (!profileMonthAmountEmpty) && (userMonthAmountCents >= pl.getMaxAmountMonth())) {
					logMsg += "amount limit reached (month) :: limit: "+pl.getMaxAmountMonth()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (month) : "+userMonthAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((pl!=null) && (pl.getMaxAmountWeek()!=null)) profileWeekAmountEmpty = false;
				if ((userWeekAmountEmpty) && (!profileWeekAmountEmpty) && (userWeekAmountCents >= pl.getMaxAmountWeek())) {
					logMsg += "amount limit reached (week) :: limit: "+pl.getMaxAmountWeek()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (week) : "+userWeekAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((pl!=null) && (pl.getMaxAmountDay()!=null)) profileDayAmountEmpty = false;
				if ((userDayAmountEmpty) && (!profileDayAmountEmpty) && (userDayAmountCents >= pl.getMaxAmountDay())) {
					logMsg += "amount limit reached (day) :: limit: "+pl.getMaxAmountDay()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (day) : "+userDayAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				logMsg += "passed!";
				log.debug(logMsg);
				
				boolean globalMonthAmountEmpty = true;
				boolean globalWeekAmountEmpty = true;
				boolean globalDayAmountEmpty = true;
				boolean globalMonthTranCountEmpty = true;
				boolean globalWeekTranCountEmpty = true;
				boolean globalDayTranCountEmpty = true;
				//Made it past profile limits (PL) .. Checking global player limits (GPL) (if no IPL/PL set)
				logMsg = "global player limits (GPL) check : ";
				if ((gpl!=null) && (gpl.getMaxTransactionsMonth()!=null)) globalMonthTranCountEmpty = false;
				if ((userMonthTranCountEmpty) && (profileMonthTranCountEmpty) && (!globalMonthTranCountEmpty) && (userMonthTranCount >= gpl.getMaxTransactionsMonth())) {
					logMsg += "transaction limit reached (month) :: limit: "+gpl.getMaxTransactionsMonth()+" player transaction count (month) : "+userMonthTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((gpl!=null) && (gpl.getMaxTransactionsWeek()!=null)) globalWeekTranCountEmpty = false;
				if ((userWeekTranCountEmpty) && (profileWeekTranCountEmpty) && (!globalWeekTranCountEmpty) && (userWeekTranCount >= gpl.getMaxTransactionsWeek())) {
					logMsg += "transaction limit reached (week) :: limit: "+gpl.getMaxTransactionsWeek()+" player transaction count (week) : "+userWeekTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((gpl!=null) && (gpl.getMaxTransactionsDay()!=null)) globalDayTranCountEmpty = false;
				if ((userDayTranCountEmpty) && (profileDayTranCountEmpty) && (!globalDayTranCountEmpty) && (userDayTranCount >= gpl.getMaxTransactionsDay())) {
					logMsg += "transaction limit reached (day) :: limit: "+gpl.getMaxTransactionsDay()+" player transaction count (day) : "+userDayTranCount;
					log.debug(logMsg);
					return false;
				}
				if ((gpl!=null) && (gpl.getMaxAmountMonth()!=null)) globalMonthAmountEmpty = false;
				if ((userMonthAmountEmpty) && (profileMonthAmountEmpty) && (!globalMonthAmountEmpty) && (userMonthAmountCents >= gpl.getMaxAmountMonth())) {
					logMsg += "amount limit reached (month) :: limit: "+gpl.getMaxAmountMonth()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (month) : "+userMonthAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((gpl!=null) && (gpl.getMaxAmountWeek()!=null)) globalWeekAmountEmpty = false;
				if ((userWeekAmountEmpty) && (profileWeekAmountEmpty) && (!globalWeekAmountEmpty) && (userWeekAmountCents >= gpl.getMaxAmountWeek())) {
					logMsg += "amount limit reached (week) :: limit: "+gpl.getMaxAmountWeek()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (week) : "+userWeekAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				if ((gpl!=null) && (gpl.getMaxAmountDay()!=null)) globalDayAmountEmpty = false;
				if ((userDayAmountEmpty) && (profileDayAmountEmpty) && (!globalDayAmountEmpty) && (userDayAmountCents >= gpl.getMaxAmountDay())) {
					logMsg += "amount limit reached (day) :: limit: "+gpl.getMaxAmountDay()+"c, player "+((isDeposit)?"deposit":"withdrawal")+" amount (day) : "+userDayAmountCents+"c";
					log.debug(logMsg);
					return false;
				}
				logMsg += "passed!";
				log.debug(logMsg);
				return true;
			} else {
				logMsg = "domain level amount limit reached!";
				if (domainMonthAmountCents >= domainAmountLimitMonth) {
					logMsg += " domain amount limit (month) : "+domainAmountLimitMonth+"c, actual "+((isDeposit)?"deposit":"withdrawal")+" amount (month) : "+domainMonthAmountCents;
				}
				if (domainWeekAmountCents >= domainAmountLimitWeek) {
					logMsg += " domain amount limit (week) : "+domainAmountLimitWeek+"c, actual "+((isDeposit)?"deposit":"withdrawal")+" amount (week) : "+domainWeekAmountCents;
				}
				if (domainDayAmountCents >= domainAmountLimitDay) {
					logMsg += " domain amount limit (day) : "+domainAmountLimitDay+"c, actual "+((isDeposit)?"deposit":"withdrawal")+" amount (day) : "+domainDayAmountCents;
				}
				log.error(logMsg);
				return false;
			}
		} else {
			logMsg = "domain level transaction limit reached!";
			if (domainMonthTranCount >= domainTranLimitMonth) {
				logMsg += " domain transaction limit (month) : "+domainTranLimitMonth+" actual transaction count (month) : "+domainMonthTranCount;
			}
			if (domainWeekTranCount >= domainTranLimitWeek) {
				logMsg += " domain transaction limit (week) : "+domainTranLimitWeek+" actual transaction count (week) : "+domainWeekTranCount;
			}
			if (domainDayTranCount >= domainTranLimitDay) {
				logMsg += " domain transaction limit (day) : "+domainTranLimitDay+" actual transaction count (day) : "+domainDayTranCount;
			}
			log.error(logMsg);
			return false;
		}
	}
}
