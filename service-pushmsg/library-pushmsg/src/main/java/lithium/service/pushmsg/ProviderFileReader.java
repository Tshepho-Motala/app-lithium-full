package lithium.service.pushmsg;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.pushmsg.client.objects.Provider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProviderFileReader {
	public Map<String, Provider> read() throws Exception {
		HashMap<String, Provider> providers = new HashMap<>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		try {
			for (Resource resource: resolver.getResources("classpath*:providers/*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					log.info("Found provider " + resource.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					Provider processor = mapper.readValue(resource.getInputStream(), Provider.class);
					providers.put(name, processor); 
				} else {
					throw new Exception("The provider file " + resource.getFilename() + " does not comply to the naming convention pattern.");
				}
			}
		} catch (FileNotFoundException fne) {
		}
		return providers;
	}
}