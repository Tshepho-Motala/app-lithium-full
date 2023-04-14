package lithium.service.translate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.data.repositories.LanguageRepository;

@RestController
@RequestMapping("/apiv1/language")
public class LanguageController {

	@Autowired LanguageRepository repository;
	
	@RequestMapping("/{id}/toggle")
	public ResponseEntity<Language> toggle(@PathVariable("id") Long id) {
		Language language = repository.findOne(id);
		if (language == null) return new ResponseEntity<Language>(HttpStatus.NOT_FOUND);
		language.setEnabled(!language.isEnabled());
		repository.save(language);
		return new ResponseEntity<Language>(language, HttpStatus.OK);
	}

}