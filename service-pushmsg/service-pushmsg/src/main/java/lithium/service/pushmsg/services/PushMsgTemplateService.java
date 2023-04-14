package lithium.service.pushmsg.services;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.pushmsg.data.entities.Domain;
import lithium.service.pushmsg.data.entities.Language;
import lithium.service.pushmsg.data.entities.PushMsgContent;
import lithium.service.pushmsg.data.entities.PushMsgHeading;
import lithium.service.pushmsg.data.entities.PushMsgTemplate;
import lithium.service.pushmsg.data.entities.PushMsgTemplateRevision;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRepository;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRevisionRepository;

@Service
public class PushMsgTemplateService {
	@Autowired DomainService domainService;
	@Autowired UserService userService;
	@Autowired PushMsgTemplateRepository pushMsgTemplateRepository;
	@Autowired PushMsgTemplateRevisionRepository pushMsgTemplateRevisionRepository;
	
	public static String DEFAULT_LANG_ISO = "en";
	
	public PushMsgTemplate findOrCreate(Domain domain, String name, String lang, String heading, String content) {
		PushMsgTemplate pushMsgTemplate = findByDomainNameAndName(domain.getName(), name);
		if (pushMsgTemplate == null) {
			pushMsgTemplate = pushMsgTemplateRepository.save(
				PushMsgTemplate.builder()
				.domain(domain)
				.name(name)
				.build()
			);
			if (lang==null) lang = DEFAULT_LANG_ISO;
			Language language = Language.builder()
			.code(lang)
			.name(lang)
			.build();
			PushMsgTemplateRevision pushMsgTemplateRevision = pushMsgTemplateRevisionRepository.save(
				PushMsgTemplateRevision.builder()
				.pushMsgHeadings(Arrays.asList(
					PushMsgHeading.builder().heading(heading).language(language).build()
				))
				.pushMsgContents(Arrays.asList(
					PushMsgContent.builder().content(content).language(language).build()
				))
				.pushMsgTemplate(pushMsgTemplate)
				.build()
			);
			pushMsgTemplate.setCurrent(pushMsgTemplateRevision);
			pushMsgTemplate = pushMsgTemplateRepository.save(pushMsgTemplate);
		}
		return pushMsgTemplate;
	}
	
	public PushMsgTemplate findByDomainNameAndName(String domainName, String name) {
		return pushMsgTemplateRepository.findByDomainNameAndName(domainName, name);
	}
}