package lithium.service.affiliate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lithium.service.accounting.client.stream.transactionlabel.TransactionLabelStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.Response;
import lithium.service.accounting.client.AccountingTransactionLabelClient;
import lithium.service.accounting.objects.TransactionLabelBasic;
import lithium.service.accounting.objects.TransactionLabelContainer;
import lithium.service.affiliate.client.exception.UserNotFoundException;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.user.client.UserApiClient;
import lithium.service.user.client.objects.Label;
import lithium.service.user.client.objects.User;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionEnrichmentService {
	
	@Autowired LithiumServiceClientFactory services;
	@Autowired TransactionLabelStream transactionLabelStream;
	
	public Optional<AccountingTransactionLabelClient> getAccountingTransactionLabelClient() {
		return getClient(AccountingTransactionLabelClient.class, "service-accounting-provider-internal");
	}
	
	public Optional<UserApiClient> getUserApiClient() {
		return getClient(UserApiClient.class, "service-user");
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

	public void enrichAccountingTransactionIfAffiliatedPlayer(Long transactionId, String ownerGuid,
			String transactionType) throws UserNotFoundException {
		UserApiClient userClient = getUserApiClient().get();
		Response<User> user = userClient.getUser(ownerGuid, null);
		List<TransactionLabelBasic> labelList = new ArrayList<>();
		if (user == null || user.getData() == null) {
			log.error("Critical error enriching user with affiliate data, user not found in user module: user:" + ownerGuid + " transactionType: " + transactionType + " transactionId: " + transactionId);
			throw new UserNotFoundException();
		}
		if (user.getData().getLabelAndValue() == null || user.getData().getLabelAndValue().get(Label.AFFILIATE_GUID_LABEL) == null) {
			addLabelIfNotNull(labelList, Label.AFFILIATED_LABEL, "no", true);
			log.debug("Found non-affiliated transaction and will process it accordingly tranid: " + transactionId + " labelList: " + labelList);
			processTran(transactionId, labelList);
			log.debug("Complete non-affiliated transaction tranid: " + transactionId + " labelList: " + labelList);
			return;
		}
		
		String affiliateGuid = user.getData().getLabelAndValue().get(Label.AFFILIATE_GUID_LABEL);
		String affiliateSecondaryGuid1 = user.getData().getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_1_LABEL);
		String affiliateSecondaryGuid2 = user.getData().getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_2_LABEL);
		String affiliateSecondaryGuid3 = user.getData().getLabelAndValue().get(Label.AFFILIATE_SECONDARY_GUID_3_LABEL);
		
		addLabelIfNotNull(labelList, Label.AFFILIATED_LABEL, "yes", true);
		addLabelIfNotNull(labelList, Label.AFFILIATE_GUID_LABEL, affiliateGuid, true);
		addLabelIfNotNull(labelList, Label.AFFILIATE_SECONDARY_GUID_1_LABEL, affiliateSecondaryGuid1, true);
		addLabelIfNotNull(labelList, Label.AFFILIATE_SECONDARY_GUID_2_LABEL, affiliateSecondaryGuid2, true);
		addLabelIfNotNull(labelList, Label.AFFILIATE_SECONDARY_GUID_3_LABEL, affiliateSecondaryGuid3, true);
		
		log.debug("Found affiliated transaction and will process it accordingly tranid: " + transactionId + " labelList: " + labelList);
		processTran(transactionId, labelList);
		log.debug("Complete affiliated transaction tranid: " + transactionId + " labelList: " + labelList);
	}
	
	private void addLabelIfNotNull(List<TransactionLabelBasic> labelList, String label, String value, boolean summarize) {
		if (value != null && !value.trim().isEmpty()) {
			labelList.add(TransactionLabelBasic.builder()
				.labelName(label)
				.labelValue(value)
				.summarize(summarize)
				.build()
			);
		}
	}
	
	private void processTran(Long transactionId, List<TransactionLabelBasic> labelList) {
//		AccountingTransactionLabelClient accountingLabelClient = getAccountingTransactionLabelClient().get();
		
		TransactionLabelContainer labelContainer = TransactionLabelContainer.builder()
		.transactionId(transactionId)
		.labelList(labelList)
		.build();
		
		try {
			//accountingLabelClient.addLabels(labelContainer);
			log.debug("Register TransactionLabelContainer: " + labelContainer);
			transactionLabelStream.register(labelContainer);
		} catch (Exception e) {
			log.error("Unable to add labels to transaction: " + labelContainer);
		}
	}
}
