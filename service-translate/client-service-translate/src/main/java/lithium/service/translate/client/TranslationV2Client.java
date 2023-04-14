package lithium.service.translate.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.translate.client.objects.ChangeSet;
import lithium.service.translate.client.objects.Language;
import org.bouncycastle.cert.ocsp.Req;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "service-translate", path = "/apiv2")
public interface TranslationV2Client {

    @RequestMapping(path = "/changesets/list", method = RequestMethod.GET)
    public List<ChangeSet> getChangeSets(@RequestParam("name") String name);

    @RequestMapping(path = "/changesets/register", method = RequestMethod.POST)
    public void registerChangeSet(@RequestParam("locale2") String locale2, @RequestParam("name") String name,
            @RequestParam("changeReference") String changeReference, @RequestParam("checksum") String checksum);

    @RequestMapping(path = "changesets/remove", method = RequestMethod.DELETE)
    public void removeChangeSet(@RequestParam("locale2") String locale2, @RequestParam("name") String name, @RequestParam("changeReference") String changeReference) throws Status500InternalServerErrorException;

    @RequestMapping(path = "changesets/remove/all", method = RequestMethod.DELETE)
    public void removeAllChangeSets(@RequestParam("name") String name);

    @RequestMapping(path = "/changesets/build-translation-cache", method = RequestMethod.GET)
    public void buildTranslationCache();

    @Cacheable(cacheNames="lithium.service.translate.services.translate2", unless="#result == null")
    @RequestMapping(path="/system/translation/translate", method=RequestMethod.GET)
    public String findByDomainAndCodeAndLocale(@RequestParam("domainName") String domainName, @RequestParam("code") String code, @RequestParam("locale") String locale);

    @RequestMapping(path = "/translations/delete", method = RequestMethod.DELETE)
    Response<String> deleteTranslationByCode(@RequestParam("code") String code);

    @Cacheable(cacheNames="lithium.service.translate.services.languages", unless="#result == null")
    @RequestMapping(path = "/system/languages/find-language-by-locale", method=RequestMethod.GET)
    public Language findLanguageByLocale(@RequestParam("locale") String locale);
}