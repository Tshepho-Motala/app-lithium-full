package lithium.service.access.services;

import java.util.Date;
import java.util.List;
import lithium.service.access.client.objects.AuthorizationRequest;
import lithium.service.access.client.objects.RawAuthorizationData;
import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.AccessControlListTransactionData;
import lithium.service.access.data.entities.AccessRuleTransaction;
import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.entities.ExternalListTransactionData;
import lithium.service.access.data.entities.RawTransactionData;
import lithium.service.access.data.repositories.AccessControlListTransactionDataRepository;
import lithium.service.access.data.repositories.AccessRuleTransactionRepository;
import lithium.service.access.data.repositories.ExternalListTransactionDataRepository;
import lithium.service.access.data.repositories.RawTransactionDataRepository;
import lithium.service.access.data.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AccessRuleService {
	@Autowired AccessRuleTransactionRepository accessRuleTransactionRepository;
	@Autowired UserRepository userRepository;
	@Autowired AccessControlListTransactionDataRepository accessControlListTransactionDataRepository;
	@Autowired RawTransactionDataRepository rawTransactionDataRepository;
	@Autowired ExternalListTransactionDataRepository externalListTransactionDataRepository;

	/**
	 * Initialization of a access rule transaction.
	 * The transaction will be bound to rule execution steps to provide a detailed breakdown of the rule workflow.
	 * @return AccessRuleTransaction
	 */
	public AccessRuleTransaction startAccessRuleTransaction(final AuthorizationRequest authorizationRequest) {
		if (authorizationRequest.getUserGuid() != null) {
      AccessRuleTransaction art = AccessRuleTransaction.builder()
          .browser(authorizationRequest.getBrowser())
          .city(authorizationRequest.getCity())
          .claimedCity(authorizationRequest.getClaimedCity())
          .country(authorizationRequest.getCountry())
          .claimedCountry(authorizationRequest.getClaimedCountry())
          .state(authorizationRequest.getState())
          .claimedState(authorizationRequest.getClaimedState())
          .creationDate(DateTime.now().toDate())
          .deviceId(authorizationRequest.getDeviceId())
          .ipAddress(authorizationRequest.getIpAddress())
          .os(authorizationRequest.getOs())
          .user(userRepository.findOrCreate(authorizationRequest.getUserGuid()))
          .build();
      log.debug("Start access rule transaction: " + art);
      art = accessRuleTransactionRepository.save(art);
      return art;
    }
		return null;
	}

	/**
	 * Each rule step needs to be saved. This is for internal list steps.
	 */
	@Transactional
	public RawTransactionData saveRuleStep(AccessRuleTransaction art, AccessControlList acl, RawAuthorizationData rawData) {
		if (art != null) {
      AccessControlListTransactionData tranData = AccessControlListTransactionData.builder()
          .accessControlList(acl)
          .accessRuleTransaction(art)
          .build();
      tranData = accessControlListTransactionDataRepository.save(tranData);
      log.debug("Saved transaction data: " + tranData);
      RawTransactionData rawTransactionData = RawTransactionData.builder()
          .accessControlListTransactionData(tranData)
          .rawRequestData(rawData.getRawRequestToProvider())
          .rawResponseData(rawData.getRawResponseFromProvider())
          .creationDate(DateTime.now().toDate())
          .build();
      rawTransactionData = rawTransactionDataRepository.save(rawTransactionData);
      log.debug("Saved raw data: (switch to trace level to see actual raw data)" + rawTransactionData.getId());
      log.trace("Saved raw data: " + rawTransactionData);
      return rawTransactionData;
    }
		return null;
	}

	/**
	 * Each rule step needs to be saved. This is for external list steps.
	 */
	@Transactional
	public RawTransactionData saveRuleStep(AccessRuleTransaction art, ExternalList externalList, RawAuthorizationData rawData) {
		if (art != null) {
      ExternalListTransactionData tranData = ExternalListTransactionData.builder()
          .externalList(externalList)
          .accessRuleTransaction(art)
          .build();
      tranData = externalListTransactionDataRepository.save(tranData);
      log.debug("Saved transaction data: " + tranData);
      RawTransactionData rawTransactionData = RawTransactionData.builder()
          .externalListTransactionData(tranData)
          .rawRequestData(rawData.getRawRequestToProvider())
          .rawResponseData(rawData.getRawResponseFromProvider())
          .creationDate(DateTime.now().toDate())
          .build();
      rawTransactionData = rawTransactionDataRepository.save(rawTransactionData);
      log.debug("Saved raw data: (switch to trace level to see actual raw data)" + rawTransactionData.getId());
      log.trace("Saved raw data: " + rawTransactionData);
      return rawTransactionData;
    }
		return null;
	}

  @Transactional
  public void deleteAllRawTransactionDataBefore(List<RawTransactionData> data) {
    rawTransactionDataRepository.deleteAll(data);
  }

  public Page<RawTransactionData> findAllRawTransactionDataBefore(Date date, PageRequest pageRequest) {
    return rawTransactionDataRepository.findAllByCreationDateBefore(date, pageRequest);
  }
}
