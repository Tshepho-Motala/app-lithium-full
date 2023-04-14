package lithium.service.cashier.machine;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.client.frontend.DoStateField;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import lithium.service.cashier.client.frontend.UserRequest;
import lithium.service.cashier.client.internal.DoProcessorResponse;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.DomainMethodProcessor;
import lithium.service.cashier.data.entities.MethodStage;
import lithium.service.cashier.data.entities.ProcessorUser;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.User;
import lithium.service.product.client.objects.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoMachineContext {
	private Long sessionId;
	private String source;
	private DomainMethod domainMethod;
	private User author;
	private User user;
	private lithium.service.user.client.objects.User externalUser;
	private lithium.service.domain.client.objects.Domain externalDomain;
	private UserRequest userRequest;
	private DoRequest request;
	private DoResponse response;
	private TransactionType type;
	private Transaction transaction;
	private DomainMethodProcessor processor;
	private Integer stage;
	private DoMachineState state;
	private MethodStage methodStage;
	private DoProcessorResponse processorResponse;
	private BigDecimal amountDifference; // This is the variance allowed when processor returns an amount, before transaction is declined.
	private Product product;
	private Object processorCallbackRequest; // This is populated when a callback is received from the processor and the callback implementation populates the relevant field ( eg. IDS)
	private ProcessorUser processorUser; //User id assigned by the processor in the current deposit or past deposit
	private String deviceId; // Blackbox device id
	private Long depositCount;
	private boolean allowMultipleTransactions;
	private boolean directWithdraw;
	private User initiationAuthor;
	private boolean transactionExpired;
	private boolean balanceLimitEscrow;
	private boolean firstDeposit;
	private Transaction linkedTransaction;

	@Default
	private Map<String, DoStateFieldGroup> inputFieldGroups = new HashMap<>();
	@Default
	private Map<String, DoStateField> outputFields = new HashMap<>();

	public void addRawRequestLog(String message) {
		if (processorResponse != null) processorResponse.addRawRequestLog(message);
	}
	public void addRawResponseLog(String message) {
		if (processorResponse != null) processorResponse.addRawResponseLog(message);
	}
	
	public String currencyCode() {
		if ((hasProductGuid()) && (product!=null)) return product.getCurrencyCode();
		return getExternalDomain().getCurrency();
	}
	
	public boolean inApp() {
		if (getDomainMethod()!=null) {
			if (getDomainMethod().getMethod()!=null) {
				if (getDomainMethod().getMethod().getInApp()!=null) {
					return getDomainMethod().getMethod().getInApp();
				}
				return false;
			}
			return false;
		}
		return false;
	}
	
	public boolean hasProductGuid() {
		if (getRequest()!=null) {
			return getRequest().hasProductGuid();
		}
		return false;
	}
	
	public boolean inAppAndHasProductGuid() {
		if (hasProductGuid() && inApp()) return true;
		return false;
	}

	public boolean reserveFundsOnWithdrawal() {
		return (getProcessor() != null &&
				getProcessor().getReserveFundsOnWithdrawal() != null &&
				getProcessor().getReserveFundsOnWithdrawal());
	}

	public boolean isWithdrawalFundsReserved() {
		return (transaction.getAccRefToWithdrawalPending() != null);
	}

	public Optional<lithium.service.user.client.objects.User> externalUser(){
		return ofNullable(this.externalUser);
	}

	public Optional<lithium.service.domain.client.objects.Domain> externalDomain(){
		return ofNullable(this.externalDomain);
	}
	public Optional<Transaction> transaction(){
		return ofNullable(this.transaction);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DoMachineContext.class.getSimpleName() + "(", ")")
				.add("sessionId=" + sessionId)
				.add("source='" + source + "'")
				.add("domainMethod=" + ofNullable(domainMethod)
						.map(dm -> "(" + dm.getId() + ", " + dm.getName() + ")")
						.orElse(null))
				.add("author=" + ofNullable(author)
						.map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
						.orElse(null))
				.add("user=" + ofNullable(user)
						.map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
						.orElse(null))
				.add("externalUser=" + ofNullable(externalUser)
						.map(u -> "(" + u.getId() + ", " + u.getGuid() + ", " + u.getUsername() + ")")
						.orElse(null))
				.add("externalDomain=" + ofNullable(externalDomain)
						.map(d -> "(" + d.getId() + ", " + d.getName() + ")")
						.orElse(null))
				.add("userRequest=" + ofNullable(userRequest)
						.map(ur -> "(" + ur.getIpAddr() + ", " + ur.getHeaders().entrySet().stream()
								.filter(entry -> !"authorization".equalsIgnoreCase(entry.getKey()))
								.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)) + ")")
						.orElse(null))
				.add("request=" + request)
				.add("response=" + response)
				.add("type=" + type)
				.add("transaction=" + transaction)
				.add("processor=" + ofNullable(processor)
						.map(dmp -> "(" + dmp.getId() + ", " + dmp.getDescription() + ")")
						.orElse(null))
				.add("stage=" + stage)
				.add("state=" + state)
				.add("methodStage.id=" + ofNullable(methodStage)
						.map(MethodStage::getId)
						.orElse(null))
				.add("processorResponse=" + ofNullable(processorResponse)
						.map(DoProcessorResponse::toStringTrim)
						.orElse(null))
				.add("amountDifference=" + amountDifference)
				.add("product=" + product)
				.add("processorCallbackRequest=" + processorCallbackRequest)
				.add("processorUser=" + ofNullable(processorUser)
						.map(u -> "(" + u.getId() + ", " + ofNullable(u.getUser()).map(User::getGuid).orElse(null) + ")")
						.orElse(null))
				.add("deviceId='" + deviceId + "'")
				.add("depositCount=" + depositCount)
				.add("allowMultipleTransactions=" + allowMultipleTransactions)
				.add("directWithdraw=" + directWithdraw)
				.add("initiationAuthor=" + ofNullable(initiationAuthor)
						.map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
						.orElse(null))
				.add("transactionExpired=" + transactionExpired)
				.add("balanceLimitEscrow=" + balanceLimitEscrow)
				.add("firstDeposit=" + firstDeposit)
				.add("linkedTransaction.id=" + ofNullable(linkedTransaction)
						.map(Transaction::getId)
						.orElse(null))
				.add("inputFieldGroups=" + inputFieldGroups)
				.add("outputFields=" + outputFields)
				.toString();
	}
}
