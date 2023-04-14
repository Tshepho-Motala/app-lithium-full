package lithium.service.sms.data.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.SMSTemplate;

public interface SMSTemplateRepository extends PagingAndSortingRepository<SMSTemplate, Long>, JpaSpecificationExecutor<SMSTemplate> {
	SMSTemplate findByDomainNameAndNameAndLang(String domainName, String name, String lang);
	Page<SMSTemplate> findByDomainName(String domainName, Pageable pageRequest);
	List<SMSTemplate> findByDomainNameAndEnabledTrue(String domainName);
	List<SMSTemplate> findByDomainNameAndLangAndEnabledTrue(String domainName, String lang);
	default SMSTemplate findOne(Long id) {
		return findById(id).orElse(null);
	}
}