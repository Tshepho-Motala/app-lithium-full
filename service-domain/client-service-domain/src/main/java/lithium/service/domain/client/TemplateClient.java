package lithium.service.domain.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.domain.client.objects.Template;

@FeignClient(name="service-domain")
public interface TemplateClient {
	@RequestMapping(path = "/{domainName}/templates/findByNameAndLangAndDomainName")
	public Response<Template> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name, @RequestParam("lang") String lang);
}
