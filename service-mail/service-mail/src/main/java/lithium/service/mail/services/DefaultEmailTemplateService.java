package lithium.service.mail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.mail.data.entities.DefaultEmailTemplate;
import lithium.service.mail.data.entities.DefaultEmailTemplatePlaceholder;
import lithium.service.mail.data.repositories.DefaultEmailTemplatePlaceholderRepository;
import lithium.service.mail.data.repositories.DefaultEmailTemplateRepository;

@Service
public class DefaultEmailTemplateService {
	@Autowired DefaultEmailTemplateRepository defaultEmailTemplateRepository;
	@Autowired DefaultEmailTemplatePlaceholderRepository defaultEmailTemplatePlaceholderRepository;
	
	public DefaultEmailTemplate findByName(String name) {
		return defaultEmailTemplateRepository.findByName(name);
	}
	
	public DefaultEmailTemplatePlaceholder findPlaceholderByDefaultEmailTemplateAndName(DefaultEmailTemplate defaultEmailTemplate, String name) {
		return defaultEmailTemplatePlaceholderRepository.findByDefaultEmailTemplateAndName(defaultEmailTemplate, name);
	}
	
	public DefaultEmailTemplate save(DefaultEmailTemplate defaultEmailTemplate) {
		return defaultEmailTemplateRepository.save(defaultEmailTemplate);
	}
	
	public DefaultEmailTemplate save(String name, String subject, String body) {
		return defaultEmailTemplateRepository.save(
			DefaultEmailTemplate.builder()
			.name(name)
			.subject(subject)
			.body(body)
			.build()
		);
	}
	
	public DefaultEmailTemplatePlaceholder savePlaceholder(DefaultEmailTemplatePlaceholder defaultEmailTemplatePlaceholder) {
		return defaultEmailTemplatePlaceholderRepository.save(defaultEmailTemplatePlaceholder);
	}
	
	public DefaultEmailTemplatePlaceholder savePlaceholder(DefaultEmailTemplate defaultEmailTemplate, DefaultEmailTemplatePlaceholder defaultEmailTemplatePlaceholder) {
		return defaultEmailTemplatePlaceholderRepository.save(
			DefaultEmailTemplatePlaceholder.builder()
			.defaultEmailTemplate(defaultEmailTemplate)
			.name(defaultEmailTemplatePlaceholder.getName())
			.description(defaultEmailTemplatePlaceholder.getDescription())
			.build()
		);
	}
}