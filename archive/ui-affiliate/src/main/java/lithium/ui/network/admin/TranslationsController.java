package lithium.ui.network.admin;

import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.translate.client.TranslationClient;

@RestController
@RequestMapping("/translations")
public class TranslationsController {
	
	@Autowired
	LithiumServiceClientFactory services;

	@PostMapping("/register")
	public String registerTranslation(@RequestParam String namespaces, @RequestParam String key, @RequestParam String value) throws Exception {
		
		TranslationClient client = services.target(TranslationClient.class);
		client.registerDefault("EN", namespaces, key, URLEncoder.encode(value, "UTF-8"));
		return "OK";
		
	}
	
}
