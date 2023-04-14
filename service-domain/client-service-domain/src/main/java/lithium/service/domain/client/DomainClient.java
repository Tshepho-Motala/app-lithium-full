package lithium.service.domain.client;

import lithium.service.Response;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.objects.DomainRevisionLabelValue;
import lithium.service.domain.client.objects.ProviderAuthClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "service-domain")
public interface DomainClient {
    /* Finders */

    @RequestMapping(value = "/domains/findAllPlayerDomains", method = RequestMethod.GET)
    public Response<List<Domain>> findAllPlayerDomains();

    @RequestMapping(value = "/domains/findAllDomains", method = RequestMethod.GET)
    public Response<Iterable<Domain>> findAllDomains();

    @RequestMapping(value = "/domains/findByName", method = RequestMethod.GET)
    public Response<Domain> findByName(@RequestParam("name") String domainName);

    @RequestMapping(value = "/domain/{domainId}/image/{name}")
    public ResponseEntity<byte[]> getImage(@PathVariable("domainId") Long domainId, @PathVariable("name") String name);

    /* Crud */

    @RequestMapping(value = "/domain/{domainName}", method = RequestMethod.GET)
    public Response<Domain> view(@PathVariable("domainName") String domainName);

    @RequestMapping(value = "/domain/{domainName}", method = RequestMethod.POST)
    public Response<Domain> save(@PathVariable("domainName") String domainName, @RequestBody Domain domain);

    @RequestMapping(value = "/domain/{domainName}/updateCurrency", method = RequestMethod.POST)
    public Response<Domain> updateCurrency(
            @PathVariable("domainName") String domainName,
            @RequestParam("symbol") String symbol,
            @RequestParam("code") String code);

    @RequestMapping(value = "/domain/{domainName}/children", method = RequestMethod.GET)
    public Response<List<Domain>> children(@PathVariable("domainName") String domainName);

    @RequestMapping(value = "/domain/{domainName}/ancestors", method = RequestMethod.GET)
    public Response<List<Domain>> ancestors(@PathVariable("domainName") String domainName);

    @RequestMapping(value = "/domain/settings/{domainName}/findCurrentSettings", method = RequestMethod.GET)
    public Response<List<DomainRevisionLabelValue>> findCurrentSettings(@PathVariable("domainName") String domainName);

    @RequestMapping(value = "/domain/settings/{domainName}/findCurrentSetting", method = RequestMethod.GET)
    public Response<DomainRevisionLabelValue> findCurrentSetting(@PathVariable("domainName") String domainName, @RequestParam("settingName") String settingName);

    @RequestMapping(value = "/system/domain/providerauthclient/{domainName}/find", method = RequestMethod.GET)
    public Response<ProviderAuthClient> findProviderAuthClient(@PathVariable("domainName") String domainName, @RequestParam("code") String code);
}
