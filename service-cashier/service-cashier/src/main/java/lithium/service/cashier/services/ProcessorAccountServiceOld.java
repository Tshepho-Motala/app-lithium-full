package lithium.service.cashier.services;

import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccountType;
import lithium.service.cashier.client.objects.TransactionRemarkType;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.ProcessorUserCard;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lithium.service.cashier.data.repositories.ProcessorAccountStatusRepository;
import lithium.service.cashier.data.repositories.ProcessorAccountTypeRepository;
import lithium.service.cashier.data.repositories.ProcessorUserCardRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Deprecated //deprecated, ProcessorAccountService should be used instead
public class ProcessorAccountServiceOld {
	@Autowired
	private UserService userService;
	@Autowired
	private TransactionService transactionService;
	@Autowired
	private CashierFrontendService cashierFrontendService;
	@Autowired
	private DomainMethodService dmService;
	@Autowired
	private ProcessorUserCardRepository processorUserCardRepository;
	@Autowired
	private ProcessorAccountStatusRepository paStatusRepository;
	@Autowired
	private ProcessorAccountTypeRepository paTypeRepository;
	@Autowired
	DomainMethodProcessorService dmpService;
	@Autowired
	LithiumServiceClientFactory serviceFactory;
	@Autowired
	ProcessorAccountTransactionService paTransactionService;
	@Autowired
	CashierService cashierService;
	@Autowired
	MessageSource messageSource;
	@Autowired
	Environment environment;

	public ProcessorUserCard saveProcessorUserCard(String userGuid, DomainMethodProcessor domainMethodProcessor, UserCard userCard) throws Exception {
		return saveProcessorUserCard(userService.findOrCreate(userGuid), domainMethodProcessor, userCard);
	}

	public ProcessorUserCard saveProcessorUserCard(User user, DomainMethodProcessor domainMethodProcessor, UserCard userCard) throws Exception {
		if (user == null || domainMethodProcessor == null || userCard == null) {
			log.error("Failed to save user card: " + userCard + " Incorrect input data. User: " + user + " DomainMethodProcessor: " + domainMethodProcessor);
			throw new Exception("Failed to save user card: " + userCard + " Incorrect input data. User: " + user + " DomainMethodProcessor: " + domainMethodProcessor);
		}

		ProcessorUserCard processorUserCard = processorUserCardRepository.findByUserAndReference(user, userCard.getReference());
		boolean isDefault = false;
		if (processorUserCard == null) {
			processorUserCard = ProcessorUserCard.builder()
					.user(user)
					.domainMethodProcessor(domainMethodProcessor)
					.reference(userCard.getReference())
					.type(paTypeRepository.findByName(ProcessorAccountType.CARD.getName()))
					.bin(userCard.getBin())
					.cardType(userCard.getCardType())
					.expiryDate(userCard.getExpiryDate())
					.scheme(userCard.getScheme())
					.fingerprint(userCard.getFingerprint())
					.providerData(userCard.getProviderData())
					.name(userCard.getName())
					.status(paStatusRepository.findByName(userCard.getStatus().getName()))
					.isDefault(userCard.getIsDefault())
					.isActive(true)
					.hideInDeposit(false)
					.lastFourDigits(userCard.getLastFourDigits())
					.verified(userCard.getIsVerified())
					.build();
			processorUserCard = processorUserCardRepository.save(processorUserCard);
			log.debug("User cards with reference " + userCard.getReference() + "was successfully saved . UserCard: " + userCard);
		} else {
			isDefault = processorUserCard.getIsDefault();
			log.debug("User cards with reference " + userCard.getReference() + "is already exists. UserCard: " + userCard);
		}

		if (!isDefault && userCard.getIsDefault()) {
			log.debug("Set user card with reference: " + userCard.getReference() + " as default");
			setDefaultProcessorUserCard(user, userCard.getReference());
		}
		return processorUserCard;
	}

	public UserCard getUserCard(User user, String cardReference) throws Exception {
		if (cardReference == null || user == null) {
			log.error("Failed to get user card. Incorrect input data. User: " + user + " Card reference: " + cardReference);
			throw new Exception("Failed to get user cards. Incorrect input data.");
		}

		ProcessorUserCard processorUserCard = processorUserCardRepository.findByUserAndReference(user, cardReference);

		return processorUserCard != null ?
				UserCard.builder()
						.reference(processorUserCard.getReference())
						.cardType(processorUserCard.getCardType())
						.lastFourDigits(processorUserCard.getLastFourDigits())
						.bin(processorUserCard.getBin())
						.scheme(processorUserCard.getScheme())
						.fingerprint(processorUserCard.getFingerprint())
						.providerData(processorUserCard.getProviderData())
						.name(processorUserCard.getName())
						.isDefault(processorUserCard.getIsDefault())
						.status(PaymentMethodStatusType.fromName(processorUserCard.getStatus().getName()))
						.expiryDate(processorUserCard.getExpiryDate()).build() : null;
	}

	public List<UserCard> getUserCardsPerMethodCode(String methodCode, boolean deposit, String userName, String userGuid,
													String domainName, String ipAddress, String userAgent) throws Exception {
		log.debug("User cards are requested for: methodCode: (" +
				"domainName: " + domainName + "; method: " + (deposit ? "deposit" : "withdrawal") +
				"; methodCode: " + methodCode + "; userName: " + userName + "; userGuid: " + userGuid + "; ipAddress: " + ipAddress + "; userAgent:" + userAgent);

		User user = userService.findOrCreate(userGuid);
		DomainMethodProcessor processor = cashierFrontendService.firstEnabledProcessor(domainName, methodCode, deposit,
						user, ipAddress, userAgent)
				.orElseThrow(() -> new Exception("No processors for this method"));

		return getProcessorUserCards(user, processor);
	}


	public ProcessorUserCard validateUserCardReference(String userGuid, String cardReference) throws Exception {
		if (cardReference == null || cardReference.isEmpty()) {
			throw new Exception("Invalid input data: " + cardReference + " User: " + userGuid);
		}

		ProcessorUserCard processorUserCard = processorUserCardRepository.findByUserAndReference(userService.findOrCreate(userGuid), cardReference);

		if (processorUserCard == null) {
			throw new Exception("User card with reference: " + cardReference + " is not found for user: " + userGuid);
		} else {
			log.debug("User card with reference: " + cardReference + " User guid: " + userGuid + " is valid.");
			return processorUserCard;
		}
	}

	public List<UserCard> getProcessorUserCards(User user, DomainMethodProcessor domainMethodProcessor) throws Exception {
		if (user == null || domainMethodProcessor == null) {
			log.error("Failed to get user cards. Incorrect input data. User: " + user + " DomainMethodProcessor: " + domainMethodProcessor);
			throw new Exception("Failed to get user cards. Incorrect input data.");
		}

		log.debug("User cards are requested for user: " + user, " " + "DomainMethodProcessor id: " + domainMethodProcessor.getId());
		return processorUserCardRepository.findByUserAndDomainMethodProcessor(user, domainMethodProcessor).stream()
				.map(c -> UserCard.builder()
						.reference(c.getReference())
						.cardType(c.getCardType())
						.lastFourDigits(c.getLastFourDigits())
						.bin(c.getBin())
						.scheme(c.getScheme())
						//.fingerprint(c.getFingerprint())
						//.providerData(c.getProviderData())
						.name(c.getName())
						.isDefault(c.getIsDefault())
						.status(PaymentMethodStatusType.fromName(c.getStatus().getName()))
						.expiryDate(c.getExpiryDate()).build())
				.collect(Collectors.toList());
	}

	public void setDefaultProcessorUserCard(User user, String reference) throws Exception{
		if (reference == null || reference.isEmpty())
			throw new Exception("Failed to set default user card. Card reference is not specified");

		ProcessorUserCard processorUserCard = processorUserCardRepository.findByUserAndReference(user, reference);
		if (processorUserCard != null) {
			List<ProcessorUserCard> processorUserCards = processorUserCardRepository.findByUserAndDomainMethodProcessor(processorUserCard.getUser(), processorUserCard.getDomainMethodProcessor());
			processorUserCards.forEach(c -> c.setIsDefault(c.getReference().equals(reference)));
			processorUserCardRepository.saveAll(processorUserCards);
		}
	}

	public boolean addUserCardTransactionRemark(Long transactionId, String cardReference, UserCard userCard, TransactionRemarkType remarkType) {
		try {
			Transaction transaction = transactionService.findById(transactionId);
			if (transaction == null) throw new Exception("Invalid transaction ID " + transactionId);

			if (cardReference != null) {
				userCard = getUserCard(transaction.getUser(), cardReference);
				if (userCard == null) throw new Exception("No card with cardReference: " + cardReference + ". TransactionId: " + transactionId);
			}

			StringBuilder remark = new StringBuilder("Provider Card Information: CardType: " + userCard.getScheme());
			remark.append(userCard.getCardType() != null ? "_" + userCard.getCardType() : "");
			remark.append(", BIN: " + userCard.getBin());
			if (userCard.getBank() != null) {
				remark.append(", Bank: " + userCard.getBank());
			}
			remark.append(", Last4Digits: " + userCard.getLastFourDigits());
			remark.append(", NameOnCard: " + userCard.getName());
			remark.append(", ExpiryDate: " + userCard.getExpiryDate());
			remark.append(", IssuingCountry: " + userCard.getIssuingCountry() + ".");
			transactionService.addTransactionRemark(transaction, transaction.getUser().getGuid(), remark.toString(), remarkType);
		} catch (Exception ex) {
			log.error("Failed to add user card remark. TransactionId: " + transactionId + " Card reference:" + cardReference != null ? cardReference : userCard + " Exception " + ex.getMessage(), ex);
			return false;
		}
		return true;
	}

	public ProcessorUserCard getProcessorUserCardById(Long processorAccountId) {
		return processorUserCardRepository.findOne(processorAccountId);
	}

	public List<UserCard> getUserCards(String userGuid, String domainMethodId) {
		User user = userService.find(userGuid);
		DomainMethod domainMethod = dmService.find(Long.parseLong(domainMethodId));
		List<ProcessorUserCard> processorUserCards = getProcessorUserCards(user, domainMethod);
		List<UserCard> userCards = mapProcessorUserCardsToUserCards(processorUserCards);
		log.debug("processorUserCards: {}, userCards: {}", processorUserCards, userCards);
		return userCards;
	}

	private List<ProcessorUserCard> getProcessorUserCards(User user, DomainMethod domainMethod) {
		return processorUserCardRepository.findByUser(user)
				.stream()
				.filter(p -> p.getDomainMethodProcessor().getProcessor().getCode().equals(domainMethod.getMethod().getCode()))
				.filter(p -> p.getLastFourDigits() != null)
				.collect(Collectors.toList());
	}

	private List<UserCard> mapProcessorUserCardsToUserCards(List<ProcessorUserCard> processorUserCards) {
		return processorUserCards.stream()
				.map(c -> UserCard.builder()
						.reference(c.getReference())
						.cardType(c.getCardType())
						.lastFourDigits(c.getLastFourDigits())
						.bin(c.getBin())
						.scheme(c.getScheme())
						//.fingerprint(c.getFingerprint())
						//.providerData(c.getProviderData())
						.name(c.getName())
						.isDefault(c.getIsDefault())
						.status(PaymentMethodStatusType.fromName(c.getStatus().getName()))
						.cardName(c.getDomainMethodProcessor().getProcessor().getCode() + "****" + c.getLastFourDigits())
						.expiryDate(c.getExpiryDate()).build()).collect(Collectors.toList());
	}
}
