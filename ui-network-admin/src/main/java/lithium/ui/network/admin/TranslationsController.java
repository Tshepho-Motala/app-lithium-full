package lithium.ui.network.admin;

import java.net.URLEncoder;
import lithium.service.translate.client.objects.Domain;
import lithium.service.translate.client.stream.TranslationsStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/translations")
public class TranslationsController {
	
	@Autowired TranslationsStream translationsStream;

	@PostMapping("/register")
	public String registerTranslation(@RequestParam String namespaces, @RequestParam String key, @RequestParam String value) throws Exception {

		translationsStream.registerTranslation(new Domain("default"), "en", namespaces + "." + key, value);
		return "OK";
	}
	
}
