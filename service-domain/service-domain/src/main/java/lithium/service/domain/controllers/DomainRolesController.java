package lithium.service.domain.controllers;

import static lithium.service.Response.Status.OK;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.domain.data.entities.Domain;
import lithium.service.domain.data.entities.DomainRole;
import lithium.service.domain.data.entities.Role;
import lithium.service.domain.data.objects.DomainRoleBasic;
import lithium.service.domain.data.repositories.DomainRepository;
import lithium.service.domain.data.repositories.DomainRoleRepository;
import lithium.service.domain.data.repositories.RoleRepository;

@RestController
@RequestMapping("/domain/{domainName}/roles")
public class DomainRolesController {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private DomainRoleRepository domainRoleRepository;
	
	@GetMapping
	public Response<Iterable<DomainRole>> list(@PathVariable("domainName") String domainName) {
		Iterable<DomainRole> all = domainRoleRepository.findByDomainNameAndDeletedFalseOrderByRole(domainName);
		return Response.<Iterable<DomainRole>>builder().data(all).status(Status.OK).build();
	}
	
	@PostMapping("/add")
	public Response<DomainRole> add(@RequestBody DomainRole domainRole) {
		return Response.<DomainRole>builder().data(domainRoleRepository.save(domainRole)).build();
	}
	
	@PostMapping("/add/all")
	public Response<?> addAll(
		@PathVariable("domainName") String domainName,
		@RequestBody List<DomainRoleBasic> domainRoleBasic
	) {
		Domain domain = domainRepository.findByName(domainName);
		domainRoleBasic.stream()
		.forEach(dr -> {
			if (dr.getEnabled()) {
				Role role = roleRepository.findByRole(dr.getRole());
				if (role == null) {
					role = roleRepository.save(Role.builder().role(dr.getRole()).build());
				}
				DomainRole domainRole = DomainRole.builder()
					.domain(domain)
					.enabled(true)
					.deleted(false)
					.role(role)
					.build();
				if (domainRoleRepository.findByDomainAndRole(domain, role) == null) {
					domainRoleRepository.save(domainRole);
				}
			}
		});
		return Response.<String>builder().status(OK).build();
	}
}
