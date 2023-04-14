package lithium.service.mail.data.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.EmailTemplate;


public interface EmailTemplateRepository extends PagingAndSortingRepository<EmailTemplate, Long>, JpaSpecificationExecutor<EmailTemplate> {
	
	EmailTemplate findByDomainNameAndNameAndLang(String domainName, String name, String lang);
	List<EmailTemplate> findByDomainNameAndEnabledTrue(String domainName);
	List<EmailTemplate> findByDomainNameAndLangAndEnabledTrue(String domainName, String lang);

	default EmailTemplate findOne(Long id) {
		return findById(id).orElse(null);
	}
}
