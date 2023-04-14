package lithium.service.pushmsg.controllers;

import java.security.Principal;
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
import lithium.service.pushmsg.data.entities.PushMsgTemplate;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRepository;
import lithium.service.pushmsg.data.repositories.PushMsgTemplateRevisionRepository;
import lithium.service.pushmsg.data.specifications.PushMsgTemplateSpecification;
import lithium.service.pushmsg.services.DomainService;

@RestController
@RequestMapping("/{domainName}/pushmsgtemplates")
public class PushMsgTemplatesController {
	@Autowired PushMsgTemplateRepository pushmsgTemplateRepository;
	@Autowired PushMsgTemplateRevisionRepository pushmsgTemplateRevisionRepository;
	@Autowired DomainService domainService;
	@Autowired ChangeLogService changeLogService;
	
	@GetMapping("/table")
	public DataTableResponse<PushMsgTemplate> table(@PathVariable String domainName, DataTableRequest request, Principal principal) {
		Specification<PushMsgTemplate> spec = Specification.where(PushMsgTemplateSpecification.domain(domainName));
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<PushMsgTemplate> s = Specification.where(PushMsgTemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, pushmsgTemplateRepository.findAll(spec, request.getPageRequest()));
	}
	
	@GetMapping("/list")
	public Response<List<PushMsgTemplate>> list(@PathVariable String domainName, Principal principal) {
		return Response.<List<PushMsgTemplate>>builder().data(pushmsgTemplateRepository.findByDomainNameAndEnabledTrue(domainName)).status(Status.OK).build();
	}
	
	@GetMapping("/find")
	public Response<PushMsgTemplate> findByNameAndDomainName(
		@PathVariable("domainName") String domainName,
		@RequestParam("name") String name
	) {
		PushMsgTemplate pushmsgTemplate = pushmsgTemplateRepository.findByDomainNameAndName(domainName, name);
		if (pushmsgTemplate == null) return Response.<PushMsgTemplate>builder().status(Status.NOT_FOUND).build();
		return Response.<PushMsgTemplate>builder().data(pushmsgTemplate).build();
	}
	
	@PostMapping
	@Transactional
	public Response<PushMsgTemplate> add(@PathVariable String domainName, @RequestBody PushMsgTemplate t, Principal principal) throws Exception {
		t.setDomain(domainService.findOrCreate(t.getDomain().getName()));
		t = pushmsgTemplateRepository.save(t);
		t.getCurrent().setPushMsgTemplate(t);
		t.setCurrent(pushmsgTemplateRevisionRepository.save(t.getCurrent()));
		
		t = pushmsgTemplateRepository.save(t);
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(t, new PushMsgTemplate(), 
				new String[] { "name", "editStartedOn", "editBy", "domain", "enabled" });
		changeLogService.registerChangesWithDomain("pushmsgtemplate", "create", t.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.COMMUNICATIONS, 0, domainName);
		
		return Response.<PushMsgTemplate>builder().data(t).build();
	}
}
