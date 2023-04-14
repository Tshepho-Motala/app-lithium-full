package lithium.service.mail.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.entities.EmailTemplateRevision;

public interface EmailTemplateRevisionRepository extends PagingAndSortingRepository<EmailTemplateRevision, Long> {
	
	EmailTemplateRevision findByEmailTemplate(EmailTemplate template);

	default EmailTemplateRevision findOne(Long id) {
		return findById(id).orElse(null);
	}
}
