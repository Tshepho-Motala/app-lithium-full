package lithium.service.sms.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.sms.client.objects.DefaultSMSTemplate;
import lithium.service.sms.client.stream.DefaultSMSTemplateStream;
import lithium.util.JsonStringify;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DefaultSMSTemplateRegisterService {
	@Autowired DefaultSMSTemplateFileReader defaultSMSTemplateFileReader;
	@Autowired DefaultSMSTemplateStream defaultSMSTemplateStream;
	
	public void registerDefaultSMSTemplatesFromClasspath() throws Exception {
		Map<String, DefaultSMSTemplate> defaultSMSTemplates = defaultSMSTemplateFileReader.read();
		for (DefaultSMSTemplate defaultSMSTemplate : defaultSMSTemplates.values()) {
			log.info("DefaultSMSTemplate " + defaultSMSTemplate.getName());
			log.debug("DefaultSMSTemplate " + defaultSMSTemplate.getName() + " json: \n" + JsonStringify.objectToString(defaultSMSTemplate));
			defaultSMSTemplateStream.process(defaultSMSTemplate);
		}
	}
}