package lithium.service.sms.controllers;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.sms.data.entities.DefaultSMSTemplate;
import lithium.service.sms.data.repositories.DefaultSMSTemplateRepository;
import lithium.service.sms.data.specifications.DefaultSMSTemplateSpecification;

@RestController
@RequestMapping("/defaultsmstemplates")
public class DefaultSMSTemplatesController {
	@Autowired DefaultSMSTemplateRepository repository;
	
	@GetMapping("/table")
	public DataTableResponse<DefaultSMSTemplate> table(DataTableRequest request, Principal principal) {
		Specification<DefaultSMSTemplate> spec = null;
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<DefaultSMSTemplate> s = Specification.where(DefaultSMSTemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, repository.findAll(spec, request.getPageRequest()));
	}
	
	@GetMapping("/{id}")
	public Response<DefaultSMSTemplate> get(@PathVariable("id") Long id) {
		return Response.<DefaultSMSTemplate>builder().data(repository.findOne(id)).status(Status.OK).build();
	}
}