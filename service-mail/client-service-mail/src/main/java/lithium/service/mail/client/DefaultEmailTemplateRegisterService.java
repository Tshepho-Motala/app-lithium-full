package lithium.service.mail.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.mail.client.objects.DefaultEmailTemplate;
import lithium.service.mail.client.stream.DefaultEmailTemplateStream;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultEmailTemplateRegisterService {
	@Autowired DefaultEmailTemplateFileReader defaultEmailTemplateFileReader;
	@Autowired DefaultEmailTemplateStream defaultEmailTemplateStream;
	
	public void registerDefaultEmailTemplatesFromClasspath() throws Exception {
		Map<String, DefaultEmailTemplate> defaultEmailTemplates = defaultEmailTemplateFileReader.read();
		for (DefaultEmailTemplate defaultEmailTemplate : defaultEmailTemplates.values()) {
			log.info("DefaultEmailTemplate " + defaultEmailTemplate.getName());
			log.debug("DefaultEmailTemplate " + defaultEmailTemplate.getName() + " json: \n" + JsonStringify.objectToString(defaultEmailTemplate));
			defaultEmailTemplateStream.process(defaultEmailTemplate);
		}
	}
}