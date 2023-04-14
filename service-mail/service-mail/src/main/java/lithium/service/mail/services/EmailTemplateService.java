package lithium.service.mail.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.mail.data.entities.Domain;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.entities.EmailTemplateRevision;
import lithium.service.mail.data.entities.User;
import lithium.service.mail.data.repositories.EmailTemplateRepository;
import lithium.service.mail.data.repositories.EmailTemplateRevisionRepository;

@Service
public class EmailTemplateService {

	@Autowired DomainService domainService;
	@Autowired UserService userService;
	
	@Autowired EmailTemplateRepository repository;
	@Autowired EmailTemplateRevisionRepository revisionRepository;
	
	public EmailTemplate findOrCreate(Domain domain, User author, String name, String lang, String subject, String body) {	
		
		EmailTemplate template = repository.findByDomainNameAndNameAndLang(domain.getName(), name, lang);

		if (template == null) {
			
			template = repository.save(EmailTemplate.builder()
					.name(name)
					.domain(domain)
					.lang("en")
			.build());

			EmailTemplateRevision revision = revisionRepository.save(EmailTemplateRevision.builder()
					.subject(subject)
					.body(body)
					.emailTemplate(template)
			.build());
			
			template.setCurrent(revision);
			repository.save(template);

		}
		
		return template;
	}
	
	public EmailTemplate findByDomainNameAndNameAndLang(String domain, String name, String lang) {
		return repository.findByDomainNameAndNameAndLang(domain, name, lang);
	}
	
	public EmailTemplate findById(Long id) { 
		return repository.findById(id).orElse(null);
	}

}
