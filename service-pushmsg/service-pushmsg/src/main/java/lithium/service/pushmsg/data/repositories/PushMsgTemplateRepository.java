package lithium.service.pushmsg.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.pushmsg.data.entities.PushMsgTemplate;

public interface PushMsgTemplateRepository extends PagingAndSortingRepository<PushMsgTemplate, Long>, JpaSpecificationExecutor<PushMsgTemplate> {
	PushMsgTemplate findByDomainNameAndName(String domainName, String name);
	Page<PushMsgTemplate> findByDomainName(String domainName, Pageable pageRequest);
	List<PushMsgTemplate> findByDomainNameAndEnabledTrue(String domainName);
	default PushMsgTemplate findOne(Long id) {
		return findById(id).orElse(null);
	}
}