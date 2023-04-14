package lithium.service.mail.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.mail.data.entities.DefaultEmailTemplate;
import lithium.service.mail.data.entities.DefaultEmailTemplatePlaceholder;
import lithium.service.mail.services.DefaultEmailTemplateService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(DefaultEmailTemplateSink.class)
@Slf4j
public class DefaultEmailTemplateProcessor {
	@Autowired DefaultEmailTemplateService defaultEmailTemplateService;
	
	@StreamListener(DefaultEmailTemplateSink.INPUT) 
	void handle(lithium.service.mail.client.objects.DefaultEmailTemplate defaultEmailTemplate) throws Exception {
		log.debug("Received default email template from queue for processing :: " + defaultEmailTemplate.toString());
		DefaultEmailTemplate t = defaultEmailTemplateService.findByName(defaultEmailTemplate.getName());
		if (t == null) t = new DefaultEmailTemplate();
		t.setName(defaultEmailTemplate.getName());
		t.setSubject(defaultEmailTemplate.getSubject());
		t.setBody(defaultEmailTemplate.getBody());
		t = defaultEmailTemplateService.save(t);
		for (lithium.service.mail.client.objects.DefaultEmailTemplatePlaceholder p: defaultEmailTemplate.getPlaceholders()) processPlaceholder(p, t);
	}
	
	private DefaultEmailTemplatePlaceholder processPlaceholder(lithium.service.mail.client.objects.DefaultEmailTemplatePlaceholder p, DefaultEmailTemplate t) {
		DefaultEmailTemplatePlaceholder detp = defaultEmailTemplateService.findPlaceholderByDefaultEmailTemplateAndName(t, p.getName());
		if (detp == null) detp = DefaultEmailTemplatePlaceholder.builder().defaultEmailTemplate(t).name(p.getName()).description(p.getDescription()).build();
		detp = defaultEmailTemplateService.savePlaceholder(detp);
		return detp;
	}
}