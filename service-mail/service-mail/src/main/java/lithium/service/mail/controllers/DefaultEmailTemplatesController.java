package lithium.service.mail.controllers;

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
import lithium.service.mail.data.entities.DefaultEmailTemplate;
import lithium.service.mail.data.repositories.DefaultEmailTemplateRepository;
import lithium.service.mail.data.specifications.DefaultEmailTemplateSpecification;

@RestController
@RequestMapping("/defaultemailtemplates")
public class DefaultEmailTemplatesController {
	@Autowired DefaultEmailTemplateRepository repository;
	
	@GetMapping("/table")
	public DataTableResponse<DefaultEmailTemplate> table(DataTableRequest request, Principal principal) {
		Specification<DefaultEmailTemplate> spec = null;
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<DefaultEmailTemplate> s = Specification.where(DefaultEmailTemplateSpecification.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		return new DataTableResponse<>(request, repository.findAll(spec, request.getPageRequest()));
	}
	
	@GetMapping("/{id}")
	public Response<DefaultEmailTemplate> get(@PathVariable("id") Long id) {
		return Response.<DefaultEmailTemplate>builder().data(repository.findOne(id)).status(Status.OK).build();
	}
}