package lithium.service.domain.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRole;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.DomainRoleRepository;

@RestController
@RequestMapping("/domain/{domainName}/{domainRoleId}")
public class DomainRoleController {
	@Autowired
	private DomainRoleRepository domainRoleRepository;
//	@Autowired
//	private DomainRepository domainRepository;
	
	@PostMapping("/edit")
	public Response<DomainRole> edit(
		@PathVariable("domainName") String domainName,
		@PathVariable("domainRoleId") DomainRole domainRole
	) throws Exception {
		domainRole = domainRoleRepository.save(domainRole);
		return Response.<DomainRole>builder().data(domainRole).status(Status.OK).build();
	}
	
	@PostMapping("/enabled/{enabled}")
	public Response<DomainRole> enabled(
		@PathVariable("domainName") String domainName,
		@PathVariable("domainRoleId") DomainRole domainRole,
		@PathVariable("enabled") Boolean enabled
	) throws Exception {
		domainRole.setEnabled(enabled);
		domainRoleRepository.save(domainRole);
		return Response.<DomainRole>builder().data(domainRole).status(Status.OK).build();
	}
	
	@PostMapping("/delete")
	public Response<DomainRole> delete(
		@PathVariable("domainName") String domainName,
		@PathVariable("domainRoleId") DomainRole domainRole
	) throws Exception {
		domainRole.setDeleted(true);
		domainRole.setEnabled(false);
		domainRoleRepository.save(domainRole);
		return Response.<DomainRole>builder().data(domainRole).status(Status.OK).build();
	}
}