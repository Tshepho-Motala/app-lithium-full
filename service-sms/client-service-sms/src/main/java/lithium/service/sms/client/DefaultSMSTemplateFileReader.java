package lithium.service.sms.client;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.sms.client.objects.DefaultSMSTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DefaultSMSTemplateFileReader {
	public Map<String, DefaultSMSTemplate> read() throws Exception {
		HashMap<String, DefaultSMSTemplate> defaultSMSTemplates = new HashMap<>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			for (Resource resource: resolver.getResources("classpath*:smstemplates/*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					log.info("Found sms template " + resource.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					DefaultSMSTemplate defaultSMSTemplate = mapper.readValue(resource.getInputStream(), DefaultSMSTemplate.class);
					defaultSMSTemplates.put(name, defaultSMSTemplate); 
				} else {
					throw new Exception("The sms template  file " + resource.getFilename() + " does not comply to the naming convention pattern.");
				}
			}
		} catch (FileNotFoundException fne) {
		}
		return defaultSMSTemplates;
	}
}