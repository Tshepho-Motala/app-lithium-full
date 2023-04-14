package lithium.service.translate.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.translate.client.objects.LatestChangeSet;

@FeignClient(name = "service-translate", path = "/apiv1")
@Deprecated
public interface TranslationClient {

	@RequestMapping(path="/system/translation/translate", method=RequestMethod.GET)
	public String findByCodeAndLocale(@RequestParam("code") String code, @RequestParam("locale") String locale);
}
