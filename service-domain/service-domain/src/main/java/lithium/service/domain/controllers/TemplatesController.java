package lithium.service.domain.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.data.entities.Template;
import lithium.service.domain.services.TemplateService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/{domainName}/templates")
public class TemplatesController {
	@Autowired TemplateService service;
	
	@GetMapping("/table")
	public DataTableResponse<Template> table(@PathVariable String domainName, DataTableRequest request, LithiumTokenUtil tokenUtil) {
		return service.table(domainName, request);
	}
	
	@GetMapping("/findByDomainNameAndLang")
	public Response<List<Template>> findByDomainNameAndLang(@PathVariable("domainName") String domainName, @RequestParam(name="lang") String lang) {
		return Response.<List<Template>>builder().data(service.findByDomainNameAndLang(domainName, lang)).status(OK).build();
	}
	
	@GetMapping("/findByNameAndLangAndDomainName")
	public Response<Template> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name, @RequestParam("lang") String lang) {
		Template t = null;
		try {
			t = service.findByNameAndLangAndDomainName(domainName, name, lang);
			if (t != null) {
				t.setDomain(null);
			}
			return Response.<Template>builder().data(t).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(t).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping
	public Response<Template> add(@PathVariable String domainName, @RequestBody Template t, LithiumTokenUtil tokenUtil) throws Exception {
		Template template = null;
		try {
			template = service.add(domainName, t, tokenUtil);
			return Response.<Template>builder().data(template).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Template>builder().data(t).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
