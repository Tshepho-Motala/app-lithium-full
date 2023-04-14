package lithium.service.event.service;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lithium.service.user.client.utils.UserToPlaceholderBinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.client.AccountingPeriodClient;
import lithium.service.accounting.client.AccountingSummaryAccountClient;
import lithium.service.accounting.client.AccountingSummaryTransactionTypeClient;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.objects.SummaryAccountTransactionType;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.DomainClient;
import lithium.service.domain.client.objects.Domain;
import lithium.service.event.client.objects.EventStreamData;
import lithium.service.event.entities.Event;
import lithium.service.event.repositories.EventRepository;
import lithium.service.mail.client.MailClient;
import lithium.service.mail.client.objects.EmailData;
import lithium.service.mail.client.objects.EmailTemplate;
import lithium.service.mail.client.stream.MailStream;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.UserEventClient;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.objects.UserEvent;
import lombok.extern.slf4j.Slf4j;



@Service
@Slf4j
public class EventService {
	public static final String LANGUAGE_CODE_STRING = "en";
	public static final String ZERO_BALANCE_TEMPLATE_NAME = "email.zero.balance.deposit.number.";
	public static final long BALANCE_CHECK_DELAY_MS = 5000L;
	
	@Autowired LithiumServiceClientFactory services;
	@Autowired private AuxService auxService;
	@Autowired private EventRepository eventRepository;
	@Autowired private MailStream mailStream;
	
	public Optional<AccountingSummaryTransactionTypeClient> getAccountingSummaryTransactionTypeClient() {
		return getClient(AccountingSummaryTransactionTypeClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<AccountingSummaryAccountClient> getAccountingSummaryAccountClient() {
		return getClient(AccountingSummaryAccountClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<AccountingPeriodClient> getAccountingPeriodClient() {
		return getClient(AccountingPeriodClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<AccountingClient> getAccountingClient() {
		return getClient(AccountingClient.class, "service-accounting");
	}
	
	public Optional<UserApiClient> getUserApiClient() {
		return getClient(UserApiClient.class, "service-user");
	}
	
	public Optional<UserEventClient> getUserEventClient() {
		return getClient(UserEventClient.class, "service-user");
	}
	
	public Optional<MailClient> getMailClient() {
		return getClient(MailClient.class, "service-mail");
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
	
	@Async
	public void zeroBalanceEventHandler(EventStreamData data) {
		try {
			Thread.sleep(BALANCE_CHECK_DELAY_MS);
		} catch (InterruptedException iex) {}
		Response<User> userResponse = getUserApiClient().get().getUser(data.getOwnerGuid(), null);
		Response<Domain> domainResponse = getDomainClient().get().findByName(data.getDomainName());
		
		Long playerBalance = getTotalCustomerBalance(data.getCurrencyCode(), data.getDomainName(), data.getOwnerGuid());
		if (playerBalance != null && playerBalance <= 0L) {
			Long depositCount = getDepositCount(data.getCurrencyCode(), data.getOwnerGuid(), data.getDomainName());
			if (depositCount != null) {
				findAndDispatchNotifications(data, depositCount, userResponse.getData(), domainResponse.getData());
			}
		}
	}
	
	private Set<Placeholder> constructEmailTemplatePlaceholders(User user, Domain domain) {
		Set<Placeholder> placeholders = new HashSet<>();
		if (user != null) {
			placeholders.addAll(new UserToPlaceholderBinder(user).completePlaceholders());
		}
		if (domain != null) {
			placeholders.addAll(new DomainToPlaceholderBinder(domain).completePlaceholders());
		}
		return placeholders;
	}
	
	private void findAndDispatchNotifications(EventStreamData data, Long depositCount, User user, Domain domain) {
		String emailTemplateName = ZERO_BALANCE_TEMPLATE_NAME + (depositCount + 1L); //email template name for the next deposit bonus a player is eligible for
		Response<EmailTemplate> templateResponse = getMailClient().get().findByNameAndLangAndDomainName(data.getDomainName(), emailTemplateName, LANGUAGE_CODE_STRING);
		if (templateResponse.getStatus() == Status.OK) {
			data.setDuplicateEventPreventionKey(emailTemplateName);
			if (!isEventAlreadyHandled(data)) {
				if (templateResponse.getData().getName().equalsIgnoreCase(emailTemplateName)) {
					mailStream.process(EmailData.builder()
							.authorSystem()
							.emailTemplateLang(LANGUAGE_CODE_STRING)
							.emailTemplateName(emailTemplateName)
							.priority(1)
							.to(user.getEmail())
							.userGuid(data.getOwnerGuid())
							.placeholders(constructEmailTemplatePlaceholders(user, domain))
							.domainName(data.getDomainName())
							.build()
					);
					writeEventToDb(data);
					dispatchZeroBalanceUserEvent(data);
				}
			}
		}
	}
	
	private void writeEventToDb(EventStreamData data) {
		
		if (data.getDuplicateEventPreventionKey() == null || data.getDuplicateEventPreventionKey().trim().isEmpty()) {
			log.debug("Not persisting event due to no dedup key in request payload: " + data.toString());
			return;
		}
		
		Event event = Event.builder()
				.user(auxService.findOrCreateUser(data.getOwnerGuid()))
				.domain(auxService.findOrCreateDomain(data.getDomainName()))
				.eventType(auxService.findOrCreateEventType(data.getEventType()))
				.currency(auxService.findOrCreateCurrency(data.getCurrencyCode()))
				.duplicateEventPreventionKey(data.getDuplicateEventPreventionKey())
				.build();
		
		eventRepository.save(event);
	}

	private Long getTotalCustomerBalance(String currency, String domainName, String userGuid) {
		long balance = 0L;
		Response<Long> bonusBalance = null;
		Response<Long> playerBalance = null;
		try {
			playerBalance = getAccountingClient().get().getByOwnerGuid(
				domainName,
				"PLAYER_BALANCE",
				"PLAYER_BALANCE",
				currency,
				userGuid
			);
			
			if (playerBalance != null && playerBalance.getStatus() == Status.OK) {
				balance += playerBalance.getData();
			}
			
			bonusBalance = getAccountingClient().get().getByOwnerGuid(
				domainName,
				"PLAYER_BALANCE_CASINO_BONUS",
				"PLAYER_BALANCE",
				currency,
				userGuid
			);
			
			if (bonusBalance != null && bonusBalance.getStatus() == Status.OK) {
				balance += bonusBalance.getData();
			}
		} catch (Exception e) {
			log.error("Could not get balance for user: " + userGuid + " and currency: " + currency + " on domain: " + domainName);
			return null;
		}
		
		return balance;
	}
	
	private Long getDepositCount(String currencyCode, String ownerGuid, String domainName) {
		Response<SummaryAccountTransactionType> depositSummary = null;
		try {
			depositSummary = getAccountingSummaryTransactionTypeClient().get().find(CashierTranType.DEPOSIT.toString(), domainName, URLEncoder.encode(ownerGuid, "UTF-8"), Period.GRANULARITY_TOTAL, currencyCode);
		} catch (Exception e) {
			log.error("Problem getting deposit count for event. ownerGuid: " + ownerGuid, e);
		}
		if (depositSummary != null && depositSummary.getStatus() == Status.OK) {
			long depositCount = 0;
			if (depositSummary.getData() != null) {
				depositCount = depositSummary.getData().getTranCount();
			}
			return depositCount;
		}
		
		return null;
	}

	public boolean isEventAlreadyHandled(EventStreamData entry) {
		
		if (entry.getDuplicateEventPreventionKey() == null || entry.getDuplicateEventPreventionKey().trim().isEmpty()) {
			log.debug("Not checking for previous execution of event due to no dedup key in request payload: " + entry.toString());
			return false;
		}
		
		Event event = eventRepository.findByUserGuidAndEventTypeCodeAndCurrencyCodeAndDuplicateEventPreventionKey(entry.getOwnerGuid(), entry.getEventType(), entry.getCurrencyCode(), entry.getDuplicateEventPreventionKey());
		
		if (event == null) return false;
		
		return true;
	}
	
	public void dispatchZeroBalanceUserEvent(EventStreamData entry) {
		UserEventClient cl;
		try {
			cl = services.target(UserEventClient.class, "service-user", true);

			cl.streamUserEvent(
				entry.getDomainName(),
				entry.getOwnerGuid().substring(entry.getOwnerGuid().indexOf("/") + 1),
				UserEvent.builder()
				.type("ZERO_BALANCE")
				.data(new ObjectMapper().writeValueAsString(entry))
				.build()
				);
		} catch (Exception e) {
			log.warn("Problem dispatching zero balance user event to user service: " + entry.toString(), e);
		}
	}
}
