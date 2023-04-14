package lithium.service.sms.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.sms.data.entities.DefaultSMSTemplate;
import lithium.service.sms.data.entities.DefaultSMSTemplatePlaceholder;
import lithium.service.sms.services.DefaultSMSTemplateService;
import lombok.extern.slf4j.Slf4j;

@Component
@EnableBinding(DefaultSMSTemplateSink.class)
@Slf4j
public class DefaultSMSTemplateProcessor {
	@Autowired DefaultSMSTemplateService defaultSMSTemplateService;
	
	@StreamListener(DefaultSMSTemplateSink.INPUT) 
	void handle(lithium.service.sms.client.objects.DefaultSMSTemplate defaultSMSTemplate) throws Exception {
		log.info("Received default sms template from queue for processing :: " + defaultSMSTemplate.toString());
		DefaultSMSTemplate t = defaultSMSTemplateService.findByName(defaultSMSTemplate.getName());
		if (t == null) t = new DefaultSMSTemplate();
		t.setName(defaultSMSTemplate.getName());
		t.setDescription(defaultSMSTemplate.getDescription());
		t.setText(defaultSMSTemplate.getText());
		t = defaultSMSTemplateService.save(t);
		for (lithium.service.sms.client.objects.DefaultSMSTemplatePlaceholder p: defaultSMSTemplate.getPlaceholders()) processPlaceholder(p, t);
	}
	
	private DefaultSMSTemplatePlaceholder processPlaceholder(lithium.service.sms.client.objects.DefaultSMSTemplatePlaceholder p, DefaultSMSTemplate t) {
		DefaultSMSTemplatePlaceholder dstp = defaultSMSTemplateService.findPlaceholderByDefaultSMSTemplateAndName(t, p.getName());
		if (dstp == null) dstp = DefaultSMSTemplatePlaceholder.builder().defaultSMSTemplate(t).name(p.getName()).description(p.getDescription()).build();
		dstp = defaultSMSTemplateService.savePlaceholder(dstp);
		return dstp;
	}
}