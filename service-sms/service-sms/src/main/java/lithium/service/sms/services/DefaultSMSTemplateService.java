package lithium.service.sms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.sms.data.entities.DefaultSMSTemplate;
import lithium.service.sms.data.entities.DefaultSMSTemplatePlaceholder;
import lithium.service.sms.data.repositories.DefaultSMSTemplatePlaceholderRepository;
import lithium.service.sms.data.repositories.DefaultSMSTemplateRepository;

@Service
public class DefaultSMSTemplateService {
	@Autowired DefaultSMSTemplateRepository defaultSMSTemplateRepository;
	@Autowired DefaultSMSTemplatePlaceholderRepository defaultSMSTemplatePlaceholderRepository;
	
	public DefaultSMSTemplate findByName(String name) {
		return defaultSMSTemplateRepository.findByName(name);
	}
	
	public DefaultSMSTemplatePlaceholder findPlaceholderByDefaultSMSTemplateAndName(DefaultSMSTemplate defaultSMSTemplate, String name) {
		return defaultSMSTemplatePlaceholderRepository.findByDefaultSMSTemplateAndName(defaultSMSTemplate, name);
	}
	
	public DefaultSMSTemplate save(DefaultSMSTemplate defaultSMSTemplate) {
		return defaultSMSTemplateRepository.save(defaultSMSTemplate);
	}
	
	public DefaultSMSTemplate save(String name, String description, String text) {
		return defaultSMSTemplateRepository.save(
			DefaultSMSTemplate.builder()
			.name(name)
			.description(description)
			.text(text)
			.build()
		);
	}
	
	public DefaultSMSTemplatePlaceholder savePlaceholder(DefaultSMSTemplatePlaceholder defaultSMSTemplatePlaceholder) {
		return defaultSMSTemplatePlaceholderRepository.save(defaultSMSTemplatePlaceholder);
	}
	
	public DefaultSMSTemplatePlaceholder savePlaceholder(DefaultSMSTemplate defaultSMSTemplate, DefaultSMSTemplatePlaceholder defaultSMSTemplatePlaceholder) {
		return defaultSMSTemplatePlaceholderRepository.save(
			DefaultSMSTemplatePlaceholder.builder()
			.defaultSMSTemplate(defaultSMSTemplate)
			.name(defaultSMSTemplatePlaceholder.getName())
			.description(defaultSMSTemplatePlaceholder.getDescription())
			.build()
		);
	}
}