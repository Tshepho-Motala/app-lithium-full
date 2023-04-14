package lithium.service.cashier;

import lithium.application.LithiumShutdownSpringApplication;
import lithium.client.changelog.EnableChangeLogService;
import lithium.exceptions.EnableCustomHttpErrorCodeExceptions;
import lithium.leader.EnableLeaderCandidate;
import lithium.leader.LeaderCandidate;
import lithium.service.access.client.EnableAccessService;
import lithium.service.accounting.client.service.EnableAccountingClientService;
import lithium.service.accounting.client.stream.transactionlabel.EnableTransactionLabelStream;
import lithium.service.accounting.client.transactiontyperegister.EnableTransactionTypeRegisterService;
import lithium.service.accounting.client.transactiontyperegister.TransactionTypeRegisterService;
import lithium.service.accounting.stream.EnablePlayerBalanceReachedLimitStream;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.enums.AccountType;
import lithium.service.cashier.config.ServiceCashierConfigurationProperties;
import lithium.service.cashier.scheduled.CleanupScheduling;
import lithium.service.cashier.scheduled.CopyDomainLimits;
import lithium.service.cashier.scheduled.TranExtraFieldFillScheduling;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.TransactionService;
import lithium.service.client.EnableLithiumServiceClients;
import lithium.service.domain.client.EnableDomainClient;
import lithium.service.games.client.service.EnableGameUserStatusClientService;
import lithium.service.geo.client.stream.EnableGeoQueueStream;
import lithium.service.limit.client.EnableLimitInternalSystemClient;
import lithium.service.limit.client.stream.EnableAutoRestrictionTriggerStream;
import lithium.service.mail.client.stream.EnableMailStream;
import lithium.service.notifications.client.stream.EnableNotificationStream;
import lithium.service.product.client.stream.EnableProductPurchaseStream;
import lithium.service.rabbit.exchange.EnableLithiumRabbitExchangeCreation;
import lithium.service.raf.client.stream.EnableRAFConversionStream;
import lithium.service.sms.client.stream.EnableSMSStream;
import lithium.service.stats.client.service.EnableStatsClientService;
import lithium.service.stats.client.stream.EnableStatsStream;
import lithium.service.user.client.service.EnableUserApiInternalClientService;
import lithium.services.LithiumService;
import lithium.services.LithiumServiceApplication;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.interceptor.RetryInterceptorBuilder;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@LithiumService
@EnableAsync
@EnableScheduling
@EnableMailStream
@EnableAccessService
@EnableLeaderCandidate
@EnableLithiumServiceClients
@EnableDomainClient
@EnableChangeLogService
@EnableRAFConversionStream
@EnableProductPurchaseStream
@EnableNotificationStream
@EnableTransactionTypeRegisterService
@EnableAccountingClientService
@EnableSMSStream
@EnableStatsStream
@EnableStatsClientService
@EnableLimitInternalSystemClient
@EnableAutoRestrictionTriggerStream
@EnableUserApiInternalClientService
@EnableGameUserStatusClientService
@EnableTransactionLabelStream
@EnablePlayerBalanceReachedLimitStream
@EnableCustomHttpErrorCodeExceptions
@EnableGeoQueueStream
@EnableLithiumRabbitExchangeCreation
public class ServiceCashierApplication extends LithiumServiceApplication implements CashierTransactionLabels {
	@Autowired
	private TransactionTypeRegisterService transactionTypeService;
	@Autowired
	private CleanupScheduling cleanupScheduling;
	@Autowired
	private TranExtraFieldFillScheduling teffs;
	@Autowired
	private CopyDomainLimits cdl;
	@Autowired
	private ServiceCashierConfigurationProperties properties;
	@Autowired
	private ProcessorAccountService processorAccountService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	LeaderCandidate leaderCandidate;


	public static void main(String[] args) {
		LithiumShutdownSpringApplication.run(ServiceCashierApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(List<HttpMessageConverter<?>> messageConverters) {
		return new RestTemplate(messageConverters);
	}


	@Bean
	public TransactionService transactionService() {
		return new TransactionService();
	}

	@Bean(name = "cashier.retryLoggingInterceptor")
	public RetryOperationsInterceptor retryLoggingInterceptor() {
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.registerListener(new RetryListenerSupport() {
			@Override
			public <T, E extends Throwable > void onError (RetryContext
			context, RetryCallback < T, E > callback, Throwable throwable){
				log.warn("Retryable threw {}th exception {}",
						context.getRetryCount(), throwable.toString() + " Stack Trace: " + String.join("\\n", ExceptionUtils.getRootCauseStackTrace(throwable)));
			}
		});
		retryTemplate.setRetryPolicy(new SimpleRetryPolicy());
		return RetryInterceptorBuilder.stateless().retryOperations(retryTemplate).build();
	}

	@Bean
	public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Scheduled(fixedDelayString = "${lithium.services.cashier.cleanup-scheduling-in-milliseconds}")
	public void doTransactionCleanup() {
		//Leadership
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		log.debug("Transaction Cleanup Running : "+DateTime.now());
		cleanupScheduling.doTransactionCleanup();
	}

//	@Scheduled(fixedDelayString = "${lithium.services.cashier.transaction-extra-fields-in-milliseconds:300000}")
	public void doTransactionExtraFieldsFill() {
		//Leadership
		if (!leaderCandidate.iAmTheLeader()) {
			log.debug("I am not the leader.");
			return;
		}

		log.debug("Transaction ExtraFields Fill Starting ("+properties.getTransactionExtraFieldsInMilliseconds()+"ms) : "+DateTime.now());
		if (properties.getTransactionExtraFieldsInMilliseconds() >= 60000L) {
			log.debug("Transaction ExtraFields Fill Running : "+DateTime.now());
			teffs.doTransactionFill();
		} else {
			log.debug("Transaction ExtraFields Fill NOT Running, schedule must be more than 60s : "+DateTime.now());
		}
	}

	@EventListener
	public void startup(ApplicationStartedEvent e) throws Exception {
		log.info("Cashier context started. Registering transaction types etc.");
		super.startup(e);

		try {
			if (properties.getCopyDomainLimits()) cdl.copyDomainLimits();
		} catch (Exception ex) {
			log.error("Could not copy domain limits (DL)", ex);
		}

		//For accounting, the player balance will always be in a negative to show player has balance (plb is liability to company)
		{
			Long ttid = transactionTypeService.create(CashierTranType.PAYOUT.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.PLAYER_BALANCE_PENDING_WITHDRAWAL.value(), true, false);
			transactionTypeService.addAccount(ttid, CashierTranType.PAYOUT.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CashierTranType.PAYOUT.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PROCESSING_METHOD_LABEL, true);
			transactionTypeService.addLabel(ttid, DOMAIN_METHOD_PROCESSOR_ID, true);
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.SESSION_ID, false);
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE, true);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.DEPOSIT.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), false, true); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.DEPOSIT.value(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CashierTranType.DEPOSIT.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PROCESSING_METHOD_LABEL, true);
			transactionTypeService.addLabel(ttid, DOMAIN_METHOD_PROCESSOR_ID, true);
			transactionTypeService.addOptionalLabel(ttid, FIRST_DEPOSIT_LABEL, true);
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.FIRST_DEPOSIT_REG_SAME_DAY_LABEL, true);
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.SESSION_ID, false);
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.PLAYER_PAYMENT_METHOD_REFERENCE, true);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.DEPOSIT_FEE.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false);
			transactionTypeService.addAccount(ttid, CashierTranType.DEPOSIT_FEE.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CashierTranType.DEPOSIT_FEE.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PROCESSING_METHOD_LABEL, true);
			transactionTypeService.addLabel(ttid, DOMAIN_METHOD_PROCESSOR_ID, true);
			transactionTypeService.addLabel(ttid, FEES_FLAT, false);
			transactionTypeService.addLabel(ttid, FEES_MINIMUM, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE_FEE, false);
			transactionTypeService.addLabel(ttid, FEES_PLAYER_AMOUNT, false);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.REVERSAL_FEE.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false);
			transactionTypeService.addAccount(ttid, CashierTranType.REVERSAL_FEE.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CashierTranType.REVERSAL_FEE.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PROCESSING_METHOD_LABEL, true);
			transactionTypeService.addLabel(ttid, DOMAIN_METHOD_PROCESSOR_ID, true);
			transactionTypeService.addLabel(ttid, FEES_FLAT, false);
			transactionTypeService.addLabel(ttid, FEES_MINIMUM, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE_FEE, false);
			transactionTypeService.addLabel(ttid, FEES_PLAYER_AMOUNT, false);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.PAYOUT_FEE.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false);
			transactionTypeService.addAccount(ttid, CashierTranType.PAYOUT_FEE.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, CashierTranType.PAYOUT_FEE.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, PROCESSING_METHOD_LABEL, true);
			transactionTypeService.addLabel(ttid, DOMAIN_METHOD_PROCESSOR_ID, true);
			transactionTypeService.addLabel(ttid, FEES_FLAT, false);
			transactionTypeService.addLabel(ttid, FEES_MINIMUM, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE, false);
			transactionTypeService.addLabel(ttid, FEES_PERCENTAGE_FEE, false);
			transactionTypeService.addLabel(ttid, FEES_PLAYER_AMOUNT, false);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.CASHIER_PAYOUT_REVERSAL.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), false, true); //plb is debited (so positive is added to plb since reduces liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.PAYOUT.value(), true, false);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CashierTranType.PAYOUT.value());
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CashierTranType.CASHIER_DEPOSIT_REVERSAL.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false); //plb is credited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.DEPOSIT.value(), false, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_REVERSE_LABEL, false, CashierTranType.DEPOSIT.value());
			transactionTypeService.addLabel(ttid, PROVIDER_GUID_LABEL, true);
			transactionTypeService.addLabel(ttid, ORIGINAL_TRAN_ID, false);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.NEGATIVE_BALANCE_WRITEOFF.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, false); //plb is debited (so negative is added to plb since it is a liability to company)
			transactionTypeService.addAccount(ttid, CashierTranType.NEGATIVE_BALANCE_WRITEOFF.value(), false, true);
			transactionTypeService.addLabel(ttid, COMMENT_LABEL, false);
		}

		{
			Long ttid = transactionTypeService.create(CashierTranType.TRANSFER_TO_PLAYER_BALANCE_PENDING_WITHDRAWAL.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, AccountType.PLAYER_BALANCE.getCode());
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.SESSION_ID, false);
		}
		{
			Long ttid = transactionTypeService.create(CashierTranType.TRANSFER_FROM_PLAYER_BALANCE_PENDING_WITHDRAWAL.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, true);
			transactionTypeService.addUniqueLabel(ttid, TRAN_ID_LABEL, false, AccountType.PLAYER_BALANCE.getCode());
			transactionTypeService.addOptionalLabel(ttid, lithium.cashier.CashierTransactionLabels.SESSION_ID, false);
		}

		{
			Long ttid = transactionTypeService.create(CashierTranType.MANUAL_CASHIER_ADJUST.value()).getData().getId();
			transactionTypeService.addAccount(ttid, AccountType.PLAYER_BALANCE.getCode(), true, true);
			transactionTypeService.addAccount(ttid, AccountType.MANUAL_BALANCE_ADJUST.getCode(), true, true);
			transactionTypeService.addLabel(ttid, COMMENT_LABEL, false);
		}

		transactionTypeService.register();
		processorAccountService.setupFromEnum();
		transactionService.setupFromEnum();

	}


}
