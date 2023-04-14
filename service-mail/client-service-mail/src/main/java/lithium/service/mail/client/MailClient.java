package lithium.service.mail.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.mail.client.objects.Email;
import lithium.service.mail.client.objects.EmailTemplate;

@FeignClient(name = "service-mail")
public interface MailClient {
	@RequestMapping(path = "/mail/findOne/{id}")
	public Response<Email> findOne(@PathVariable("id") Long id);
	
	@RequestMapping(path = "/{domainName}/emailtemplates/findByNameAndLangAndDomainName")
	public Response<EmailTemplate> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name, @RequestParam("lang") String lang);

	@RequestMapping(path = "/{domainName}/emailtemplates/findByDomainName")
	public Response<EmailTemplate> findByDomainName(@PathVariable("domainName") String domainName);

}