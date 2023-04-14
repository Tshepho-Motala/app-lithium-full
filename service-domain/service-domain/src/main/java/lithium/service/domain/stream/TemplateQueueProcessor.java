package lithium.service.domain.stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.Template;
import lithium.service.domain.data.entities.TemplateRevision;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.TemplateRepository;
import lithium.service.domain.data.repositories.TemplateRevisionRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@EnableBinding(TemplateQueueSink.class)
public class TemplateQueueProcessor {
	@Autowired TemplateRepository repo;
	@Autowired TemplateRevisionRepository revisionRepo;
	@Autowired DomainRepository domainRepo;
	@Autowired ModelMapper modelMapper;
	
	@StreamListener(TemplateQueueSink.INPUT) 
	void handle(lithium.service.domain.client.objects.Template template) {
		log.info("Received template from queue for processing :: Name: " + template.getName() + ", Lang: " + template.getLang());
		Template t = repo.findByDomainNameAndNameAndLang(template.getDomain().getName(), template.getName(), template.getLang());
		if (t == null) {
			t = new Template();
			Domain domain = domainRepo.findByName(template.getDomain().getName());
			t.setDomain(domain);
			t.setName(template.getName());
			t.setLang(template.getLang());
			t.setEnabled(true);
			t = repo.save(t);
			
			TemplateRevision tr = modelMapper.map(template.getCurrent(), TemplateRevision.class);
			tr.setId(null);
			tr.setTemplate(t);
			tr = revisionRepo.save(tr);
			
			t.setCurrent(tr);
			t = repo.save(t);
		}
	}
}
