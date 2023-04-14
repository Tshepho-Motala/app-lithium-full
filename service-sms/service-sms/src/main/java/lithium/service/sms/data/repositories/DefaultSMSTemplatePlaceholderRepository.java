package lithium.service.sms.data.repositories;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.sms.data.entities.DefaultSMSTemplate;
import lithium.service.sms.data.entities.DefaultSMSTemplatePlaceholder;

public interface DefaultSMSTemplatePlaceholderRepository extends PagingAndSortingRepository<DefaultSMSTemplatePlaceholder, Long> {
	DefaultSMSTemplatePlaceholder findByDefaultSMSTemplateAndName(DefaultSMSTemplate defaultSMSTemplate, String name);
}