package lithium.service.mail.client;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.mail.client.objects.DefaultEmailTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DefaultEmailTemplateFileReader {
	public Map<String, DefaultEmailTemplate> read() throws Exception {
		HashMap<String, DefaultEmailTemplate> defaultEmailTemplates = new HashMap<>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			for (Resource resource: resolver.getResources("classpath*:emailtemplates/*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					log.info("Found email template " + resource.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					DefaultEmailTemplate defaultEmailTemplate = mapper.readValue(resource.getInputStream(), DefaultEmailTemplate.class);
					defaultEmailTemplates.put(name, defaultEmailTemplate); 
				} else {
					throw new Exception("The email template  file " + resource.getFilename() + " does not comply to the naming convention pattern.");
				}
			}
		} catch (FileNotFoundException fne) {
		}
		return defaultEmailTemplates;
	}
}