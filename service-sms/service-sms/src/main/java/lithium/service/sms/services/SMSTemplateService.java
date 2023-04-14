package lithium.service.sms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.sms.data.entities.Domain;
import lithium.service.sms.data.entities.SMSTemplate;
import lithium.service.sms.data.entities.SMSTemplateRevision;
import lithium.service.sms.data.repositories.SMSTemplateRepository;
import lithium.service.sms.data.repositories.SMSTemplateRevisionRepository;

import java.util.Date;

@Service
public class SMSTemplateService {
	@Autowired DomainService domainService;
	@Autowired UserService userService;
	@Autowired SMSTemplateRepository smsTemplateRepository;
	@Autowired SMSTemplateRevisionRepository smsTemplateRevisionRepository;
	
	public static String DEFAULT_LANG_ISO = "en";
	
	public SMSTemplate findOrCreate(Domain domain, String name, String lang, String text) {
		SMSTemplate smsTemplate = findByDomainNameAndNameAndLang(domain.getName(), name, lang);
		if (smsTemplate == null) {
			smsTemplate = smsTemplateRepository.save(SMSTemplate.builder().domain(domain).name(name).lang((lang != null)? lang: DEFAULT_LANG_ISO).build());
			SMSTemplateRevision smsTemplateRevision = smsTemplateRevisionRepository.save(SMSTemplateRevision.builder().text(text).smsTemplate(smsTemplate).build());
			smsTemplate.setCurrent(smsTemplateRevision);
            smsTemplate.setUpdatedOn(new Date());
			smsTemplate = smsTemplateRepository.save(smsTemplate);
		}
		return smsTemplate;
	}
	
	public SMSTemplate findByDomainNameAndNameAndLang(String domainName, String name, String lang) {
		return smsTemplateRepository.findByDomainNameAndNameAndLang(domainName, name, lang);
	}
}