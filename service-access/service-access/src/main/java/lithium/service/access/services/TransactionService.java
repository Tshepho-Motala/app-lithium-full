package lithium.service.access.services;

import lithium.service.access.data.entities.AccessControlList;
import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.repositories.AccessControlListTransactionDataRepository;
import lithium.service.access.data.repositories.ExternalListTransactionDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionService {
	@Autowired AccessControlListTransactionDataRepository accessControlListTransactionDataRepository;
	@Autowired ExternalListTransactionDataRepository externalListTransactionDataRepository;
	
	public boolean accessControlListHasTransactionData(AccessControlList accessControlList) {
		log.debug("Finding TranData for: "+accessControlList);
    Long count = accessControlListTransactionDataRepository.countByAccessControlList(accessControlList);
    if ((count!=null) && (count>0L)) return true;
    return false;
	}
	
	public boolean externalListHasTransactionData(ExternalList externalList) {
		log.debug("Finding TranData for: "+externalList);
    Long count = externalListTransactionDataRepository.countByExternalList(externalList);
		if ((count!=null) && (count>0L)) return true;
		return false;
	}
}
