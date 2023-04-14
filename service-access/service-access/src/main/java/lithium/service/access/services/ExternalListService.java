package lithium.service.access.services;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import lithium.service.access.data.entities.ExternalList;
import lithium.service.access.data.repositories.ExternalListRepository;
import lithium.service.access.data.repositories.ExternalListRuleStatusOptionConfigRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ExternalListService {
	@Autowired ExternalListRepository externalListRepository;
	@Autowired ExternalListRuleStatusOptionConfigRepository externalListRuleStatusOptionConfigRepository;
	
	@Transactional(rollbackOn=Exception.class)
	@Retryable(backoff=@Backoff(delay=500), maxAttempts=10)
	public void deleteExternalList(ExternalList externalList) {
		log.debug("Deleting : "+externalList);
		externalListRuleStatusOptionConfigRepository.deleteByExternalList(externalList);
		externalListRepository.delete(externalList);
	}
}
