package lithium.service.translate.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import lithium.service.translate.client.objects.Translation;
import lithium.service.translate.services.TranslationService;
import lombok.extern.slf4j.Slf4j;
import java.net.URLDecoder;

@Component
@EnableBinding(TranslationsRegisterQueueSink.class)
@Slf4j
public class TranslationsRegisterQueueProcessor {
	
	@Autowired TranslationService translationService;
	
	@StreamListener(TranslationsRegisterQueueSink.INPUT) 
	void handle(Translation translation) throws Exception {
		log.info("Received a translation from the queue for processing: " + translation);
		String lang = URLDecoder.decode(translation.getLang(), "UTF-8");
		String code = URLDecoder.decode(translation.getCode(), "UTF-8");
		String value = URLDecoder.decode(translation.getValue(), "UTF-8");

		//All translations are first being registered to the default domain - Fail save to ensure that they are registered on default domain first
		translationService.saveTranslationV2("default", lang, code, value);

		if (translation.getDomain() != null && !translation.getDomain().getName().equals("default")) {
			translationService.saveTranslationV2(translation.getDomain().getName(), lang, code, value);
		}
	}
}
