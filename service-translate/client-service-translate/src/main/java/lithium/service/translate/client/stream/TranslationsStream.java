package lithium.service.translate.client.stream;

import lithium.service.translate.client.objects.Domain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Service;
import lithium.service.translate.client.objects.Translation;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class TranslationsStream {
	
	@Autowired
	private TranslationsStreamOutputQueue channel;

	public void registerTranslation(Domain domain, String lang, String code, String value) throws UnsupportedEncodingException {
		//Encoding values as we were getting funny characters returned on the stream
		registerTranslation(Translation.builder()
				.domain(domain)
				.lang(URLEncoder.encode(lang, "UTF-8"))
				.code(URLEncoder.encode(code, "UTF-8"))
				.value(URLEncoder.encode(value, "UTF-8"))
				.build());
	}

	public void registerTranslation(Translation translation) {
		channel.channel().send(
				MessageBuilder
						.<Translation>withPayload(translation)
						.build());
	}
}