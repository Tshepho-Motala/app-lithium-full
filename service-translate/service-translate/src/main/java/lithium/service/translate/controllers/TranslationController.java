package lithium.service.translate.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.translate.services.TranslationService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/apiv1/translation")
@Slf4j
@Deprecated
public class TranslationController {

	@Autowired TranslationService service;
	
	@RequestMapping("/registerdefault")
	public void registerDefault(@RequestParam String lang, @RequestParam String namespaces, @RequestParam String key, @RequestParam String value) throws UnsupportedEncodingException {
		value = URLDecoder.decode(value, "UTF-8");
		log.info("registerdefault lang " + lang + " namespaces " + namespaces + " key '" + key + "' value '" + value + "'");
		service.saveDefault(lang, namespaces, key, value);
	}
	
	@RequestMapping("/save")
	public void save(@RequestParam String lang, @RequestParam String namespaces, @RequestParam String key, @RequestParam String value, Principal principal) {
		log.info("save lang " + lang + " namespaces " + namespaces + " key '" + key + "' value '" + value + "' principal " + principal.getName());
		service.save(lang, namespaces, key, value, principal.getName());
	}
	
	@RequestMapping("/saveByKeyId")
	public void saveByKeyId(@RequestParam String lang, @RequestParam Long keyId, @RequestParam String value, Principal principal) {
		log.info("saveByKeyId lang " + lang + " keyId " + keyId + " value '" + value + "' principal " + principal.getName());
		service.saveByKeyId(lang, keyId, value, principal.getName());
	}
}