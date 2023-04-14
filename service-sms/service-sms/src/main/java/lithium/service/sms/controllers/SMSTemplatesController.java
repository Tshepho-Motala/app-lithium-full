package lithium.service.sms.controllers;

import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import lithium.client.changelog.Category;
import lithium.client.changelog.SubCategory;
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
import lithium.service.sms.data.entities.SMSTemplate;
import lithium.service.sms.data.repositories.SMSTemplateRepository;
import lithium.service.sms.data.repositories.SMSTemplateRevisionRepository;
import lithium.service.sms.data.specifications.SMSTemplateSpecification;
import lithium.service.sms.services.DomainService;

@RestController
@RequestMapping("/{domainName}/smstemplates")
public class SMSTemplatesController {
	@Autowired SMSTemplateRepository smsTemplateRepository;
	@Autowired SMSTemplateRevisionRepository smsTemplateRevisionRepository;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping("/table")
	public DataTableResponse<SMSTemplate> table(@PathVariable String domainName, DataTableRequest request, Principal principal) {
		Specification<SMSTemplate> spec = Specification.where(SMSTemplateSpecification.domain(domainName));
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<SMSTemplate> s = Specification.where(SMSTemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, smsTemplateRepository.findAll(spec, request.getPageRequest()));
	}
	
	@GetMapping("/list")
	public List<SMSTemplate> list(@PathVariable String domainName, Principal principal) {
		return smsTemplateRepository.findByDomainNameAndEnabledTrue(domainName);
	}
	
	@GetMapping("/findByDomainNameAndLang")
	public Response<List<SMSTemplate>> findByDomainNameAndLang(@PathVariable("domainName") String domainName, @RequestParam(name="lang") String lang) {
		return Response.<List<SMSTemplate>>builder().data(smsTemplateRepository.findByDomainNameAndLangAndEnabledTrue(domainName, lang)).status(OK).build();
	}
	
	@GetMapping("/findByNameAndLangAndDomainName")
	public Response<SMSTemplate> findByNameAndLangAndDomainName(@PathVariable("domainName") String domainName, @RequestParam("name") String name, @RequestParam("lang") String lang) {
		SMSTemplate smsTemplate = smsTemplateRepository.findByDomainNameAndNameAndLang(domainName, name, lang);
		if (smsTemplate == null) return Response.<SMSTemplate>builder().status(Status.NOT_FOUND).build();
		return Response.<SMSTemplate>builder().data(smsTemplate).build();
	}
	
	@PostMapping
	@Transactional
	public Response<SMSTemplate> add(@PathVariable String domainName, @RequestBody SMSTemplate t, Principal principal) throws Exception {
		t.setDomain(domainService.findOrCreate(t.getDomain().getName()));
		t = smsTemplateRepository.save(t);
		t.getCurrent().setSmsTemplate(t);
		t.setCurrent(smsTemplateRevisionRepository.save(t.getCurrent()));
		t.setUpdatedOn(new Date());
		t = smsTemplateRepository.save(t);
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(t, new SMSTemplate(), 
				new String[] { "lang", "name", "editStartedOn", "editBy", "domain", "enabled" });
		changeLogService.registerChangesWithDomain("smstemplate", "create", t.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, domainName);
		
		return Response.<SMSTemplate>builder().data(t).build();
	}
}
