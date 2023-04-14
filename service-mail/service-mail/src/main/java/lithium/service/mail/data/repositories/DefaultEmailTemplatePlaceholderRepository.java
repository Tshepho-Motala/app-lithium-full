package lithium.service.mail.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.mail.data.entities.DefaultEmailTemplate;
import lithium.service.mail.data.entities.DefaultEmailTemplatePlaceholder;

public interface DefaultEmailTemplatePlaceholderRepository extends PagingAndSortingRepository<DefaultEmailTemplatePlaceholder, Long> {
	DefaultEmailTemplatePlaceholder findByDefaultEmailTemplateAndName(DefaultEmailTemplate defaultEmailTemplate, String name);
}