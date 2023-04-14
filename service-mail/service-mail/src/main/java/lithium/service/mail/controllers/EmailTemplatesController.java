package lithium.service.mail.controllers;

import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.mail.data.entities.EmailTemplate;
import lithium.service.mail.data.repositories.EmailTemplateRepository;
import lithium.service.mail.data.repositories.EmailTemplateRevisionRepository;
import lithium.service.mail.data.specifications.EmailTemplateSpecification;
import lithium.service.mail.services.DomainService;

@Slf4j
@RestController
@RequestMapping("/{domainName}/emailtemplates")
public class EmailTemplatesController {

	@Autowired EmailTemplateRepository repository;
	@Autowired EmailTemplateRevisionRepository revisionRepository;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping("/table")
	public DataTableResponse<EmailTemplate> table(@PathVariable String domainName, DataTableRequest request, Principal principal) {
		Specification<EmailTemplate> spec = Specification.where(EmailTemplateSpecification.domainName(domainName));
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<EmailTemplate> s = Specification.where(EmailTemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, repository.findAll(spec, request.getPageRequest()));
	}
	
	@GetMapping("/list")
	public List<EmailTemplate> list(@PathVariable String domainName, Principal principal) {
		return repository.findByDomainNameAndEnabledTrue(domainName);
	}
	
	@GetMapping("/findByDomainNameAndLang")
	public Response<List<EmailTemplate>> findByDomainNameAndLang(@PathVariable("domainName") String domainName, @RequestParam(name="lang") String lang) {
		return Response.<List<EmailTemplate>>builder().data(repository.findByDomainNameAndLangAndEnabledTrue(domainName, lang)).status(OK).build();
	}
	
	@GetMapping("/findByNameAndLangAndDomainName")
	public Response<EmailTemplate> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name, @RequestParam("lang") String lang) {
		EmailTemplate template = repository.findByDomainNameAndNameAndLang(domainName, name, lang);
		if (template == null) return Response.<EmailTemplate>builder().status(Status.NOT_FOUND).build();
		return Response.<EmailTemplate>builder().data(template).build();
	}

	@GetMapping("/findByDomainName")
	public Response<List<EmailTemplate>> findByDomainName(@PathVariable("domainName") String domainName) {
		return Response.<List<EmailTemplate>>builder().data(repository.findByDomainNameAndEnabledTrue(domainName)).status(OK).build();
	}
	
	@PostMapping
	@Transactional
	public Response<EmailTemplate> add(@PathVariable String domainName, @RequestBody EmailTemplate t, Principal principal) throws Exception {
		
		t.setDomain(domainService.findOrCreate(t.getDomain().getName()));
		t = repository.save(t);
		t.getCurrent().setEmailTemplate(t);
		t.setCurrent(revisionRepository.save(t.getCurrent()));
		t.setUpdatedOn(new Date());
		t = repository.save(t);
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(t, new EmailTemplate(), 
				new String[] { "lang", "name", "editStartedOn", "editBy", "domain", "enabled" });
		changeLogService.registerChangesWithDomain("emailtemplate", "create", t.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, domainName);
		
		return Response.<EmailTemplate>builder().data(t).build();
	}
}
