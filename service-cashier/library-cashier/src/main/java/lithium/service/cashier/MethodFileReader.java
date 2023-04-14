package lithium.service.cashier;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import lithium.service.cashier.client.objects.transaction.dto.Method;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MethodFileReader {

	public Map<String, Method> read() throws Exception {
		HashMap<String, Method> methods = new HashMap<>();
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		
		try {
			for (Resource resource: resolver.getResources("classpath*:methods/*.json")) {
				Pattern p = Pattern.compile("([a-z\\-]+)\\.json");
				Matcher m = p.matcher(resource.getFilename());
				if (m.matches()) {
					String name = m.group(1);
					log.info("Found method " + resource.getFilename());
					ObjectMapper mapper = new ObjectMapper();
					mapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
					Method method = mapper.readValue(resource.getInputStream(), Method.class);
					methods.put(name, method); 
				} else {
					throw new Exception("The method file " + resource.getFilename() + " does not comply to the naming convention pattern.");
				}
			}
		} catch (FileNotFoundException fne) {
		}

		return methods;
	}
	
}
