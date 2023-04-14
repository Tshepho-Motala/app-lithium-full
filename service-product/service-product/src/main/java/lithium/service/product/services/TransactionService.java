package lithium.service.product.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.accounting.client.AccountingClient;
import lithium.service.accounting.objects.AdjustmentTransaction;
import lithium.service.casino.client.data.BonusAllocate;
import lithium.service.casino.client.stream.TriggerBonusStream;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.gateway.client.stream.GatewayExchangeStream;
import lithium.service.product.client.objects.CashierLabels;
import lithium.service.product.client.objects.CashierTranType;
import lithium.service.product.client.objects.ProductPurchase;
import lithium.service.product.data.entities.Domain;
import lithium.service.product.data.entities.Payout;
import lithium.service.product.data.entities.Transaction;
import lithium.service.product.data.entities.User;
import lithium.service.product.data.repositories.LocalCurrencyRepository;
import lithium.service.product.data.repositories.PayoutRepository;
import lithium.service.product.data.repositories.ProductRepository;
import lithium.service.product.data.repositories.TransactionRepository;
import lithium.service.product.data.specifications.TransactionSpecifications;
import lombok.extern.slf4j.Slf4j;

import static lithium.service.user.client.objects.User.SYSTEM_GUID;

@Slf4j
@Service
public class TransactionService {

	@Autowired ProductService productService;
	@Autowired DomainService domainService;
	@Autowired UserService userService;
	@Autowired LithiumServiceClientFactory clientFactory;
	@Autowired GatewayExchangeStream gatewayExchangeStream;
	@Autowired ProductRepository productRepository;
	@Autowired LocalCurrencyRepository localCurrencyRepository;
	@Autowired PayoutRepository payoutRepository;
	@Autowired TransactionRepository transactionRepository;
	@Autowired TriggerBonusStream triggerBonusStream;
	
	private void bonusAllocate(String playerGuid, String bonusCode) {
		if (bonusCode != null && !bonusCode.isEmpty()) {
			triggerBonusStream.process(
				BonusAllocate.builder()
				.playerGuid(playerGuid)
				.bonusCode(bonusCode)
				.build()
			);
		}
	}
	
	private Optional<AccountingClient> getAccountingClient() {
		return getClient(AccountingClient.class, "service-accounting");
	}
	
	private <E> Optional<E> getClient(Class<E> theClass, String url) {
		E clientInstance = null;
		try {
			clientInstance = clientFactory.target(theClass, url, true);
		} catch (LithiumServiceClientFactoryException e) {
			log.error(e.getMessage(), e);
		}
		return Optional.ofNullable(clientInstance);
	}
	
	public Page<Transaction> findByDomains(List<String> domains, String searchValue, Pageable pageable) throws Exception {
		log.trace("findByDomains");
		Specification<Transaction> spec = Specification.where(TransactionSpecifications.domains(domains));
		if ((searchValue != null) && (searchValue.length() > 0)) {
			Specification<Transaction> s = Specification.where(TransactionSpecifications.any(searchValue));
			spec = (spec == null)? s: spec.and(s);
		}
		Page<Transaction> result = transactionRepository.findAll(spec, pageable);
		
		return result;
	}
	
	public Transaction registerProductPurchase(ProductPurchase productPurchase) throws Exception {
		log.info("registerProductPurchase : "+productPurchase);
		User user = userService.findOrCreate(productPurchase.getPlayerGuid());
		Domain domain = domainService.findOrCreate(user.domainName());
		return transactionRepository.save(
			Transaction.builder()
			.createdOn(productPurchase.getCashierTransaction().getCreatedOn())
			.amount(BigDecimal.valueOf(productPurchase.getCashierTransaction().getAmountCents()).movePointLeft(2))
			.currencyCode(productPurchase.getCashierTransaction().getCurrencyCode())
			.user(user)
			.domain(domain)
			.product(productService.find(user.domainName(), productPurchase.getProductId(), ""))
			.domainMethodId(productPurchase.getCashierTransaction().getDomainMethodId())
			.domainMethodName(productPurchase.getCashierTransaction().getDomainMethodName())
			.cashierTransactionId(productPurchase.getCashierTransaction().getTransactionId())
			.build()
		);
	}
	
	public boolean processAccountingWithdrawal(Transaction transaction, ProductPurchase productPurchase) throws Exception {
		User user = userService.findOrCreate(productPurchase.getPlayerGuid());
		Domain domain = domainService.findOrCreate(user.domainName());
		
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(CashierLabels.TRAN_ID_LABEL.value()+"="+transaction.getId());
		labelList.add(CashierLabels.CASHIER_TRAN_ID.value()+"="+productPurchase.getCashierTransaction().getTransactionId());
		labelList.add(CashierLabels.PROVIDER_GUID_LABEL.value()+"="+productPurchase.getCashierTransaction().getProcessorCode());
		labelList.add(CashierLabels.PROCESSING_METHOD_LABEL.value()+"="+productPurchase.getCashierTransaction().getMethodCode());
		labelList.add(CashierLabels.PRODUCT_ID.value()+"="+productPurchase.getProductId());
		labelList.add(CashierLabels.DOMAIN_METHOD_PROCESSOR_ID.value()+"="+productPurchase.getCashierTransaction().getDomainMethodProcessorId());
		
		Response<AdjustmentTransaction> accTran = null;
		
		accTran = getAccountingClient().get().adjust(
			(Math.abs(productPurchase.getCashierTransaction().getAmountCents()))*-1,
			new DateTime().toDateTimeISO().toString(), 
			CashierTranType.PRODUCT_PURCHASE.value(),
			CashierTranType.PRODUCT_PURCHASE.value(),
			CashierTranType.PRODUCT_PURCHASE.value(),
			labelList.toArray(new String[labelList.size()]), 
			productPurchase.getCashierTransaction().getCurrencyCode(), 
			domain.getName(), 
			user.guid(), 
			SYSTEM_GUID,
			false
		);
		
		if (accTran != null && accTran.getStatus() == Status.OK && accTran.getData().getStatus() ==
				AdjustmentTransaction.AdjustmentResponseStatus.NEW) {
			return true;
		}
		return false;
	}
	
	private void accountingPayout(Transaction transaction, Payout payout) throws Exception {
		ArrayList<String> labelList = new ArrayList<>();
		labelList.add(CashierLabels.PAYOUT_ID.value()+"="+payout.getId());
		labelList.add(CashierLabels.PRODUCT_ID.value()+"="+transaction.getProduct().getId());
		labelList.add(CashierLabels.TRAN_ID_LABEL.value()+"="+transaction.getId());
		
		getAccountingClient().get().adjust(
			payout.getCurrencyAmount().movePointRight(2).abs().longValue(),
			new DateTime().toDateTimeISO().toString(), 
			CashierTranType.PRODUCT_PAYOUT.value(),
			CashierTranType.PRODUCT_PAYOUT.value(),
			CashierTranType.PRODUCT_PAYOUT.value(),
			labelList.toArray(new String[labelList.size()]), 
			payout.getCurrencyCode(), 
			transaction.getDomain().getName(), 
			transaction.getUser().guid(), 
			SYSTEM_GUID,
			false
		);
	}

	public void payoutProduct(Transaction transaction) throws Exception {
		log.info("Successful deposit, and money removed from PB, awarding product payout");
		for (Payout payout:transaction.getProduct().getPayouts()) {
			log.info("Payout :: "+payout);
			String bonusCode = payout.getBonusCode();
			if ((bonusCode!=null) && (!bonusCode.isEmpty())) {
				log.info("Bonus payout specified, sending bonusAllocate request.");
				bonusAllocate(transaction.getUser().guid(), bonusCode);
			} else {
				log.info("Currency payout specified, sending credit request to accounting.");
				accountingPayout(transaction, payout);
			}
		}
	}
}