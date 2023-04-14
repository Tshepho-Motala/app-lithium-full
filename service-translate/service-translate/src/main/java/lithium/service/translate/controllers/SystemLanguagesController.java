package lithium.service.translate.controllers;

import lithium.service.translate.data.entities.Language;
import lithium.service.translate.services.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apiv2/system/languages")
public class SystemLanguagesController {

    @Autowired
    TranslationService translationService;

    @GetMapping("/find-language-by-locale")
    public Language findLanguageByLocale(@RequestParam("locale") String locale){
       return translationService.findLanguageByLocale(locale);
    }

}
