package lithium.service.user.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.GroupBasic;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.exceptions.Status409DuplicateGroupException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/domain/{domainName}/groups")
public class GroupsController {
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private ChangeLogService changeLogService;
	
	@GetMapping
	public Response<Iterable<Group>> list(@PathVariable("domainName") String domainName) {
		log.debug("Listing all groups");
		Iterable<Group> all = groupRepository.findByDomainNameAndDeletedFalse(domainName);
		return Response.<Iterable<Group>>builder().data(all).status(Status.OK).build();
	}
	
	@PostMapping
	public Response<Group> add(
		@PathVariable("domainName") String domainName,
		@RequestBody @Valid GroupBasic groupBasic,
		BindingResult bindingResult,
		Principal principal
	) throws Exception {
		if (bindingResult.hasErrors()) {
			return Response.<Group>builder().data2(bindingResult).status(Status.INVALID_DATA).build();
		}
		Optional<lithium.service.user.data.entities.Domain> repoDomain = Optional.ofNullable(domainRepository.findByName(groupBasic.getDomain().getName()));
		lithium.service.user.data.entities.Group group = null;

		Group groups = groupRepository.findByNameAndDomainName(groupBasic.getName(), domainName);

		if (groups != null ) {
			log.debug("Group name is not unique");
			throw new Status409DuplicateGroupException(groupBasic.getName());
		}

		if (repoDomain.isPresent()) {
			log.debug("Working with domain : "+repoDomain);
			try {
				group = groupRepository.save(
					lithium.service.user.data.entities.Group.builder()
					.name(groupBasic.getName())
					.description(groupBasic.getDescription())
					.enabled(false)
					.deleted(false)
					.domain(repoDomain.get())
					.build()
				);
				log.debug("group : "+group);
				
				List<ChangeLogFieldChange> clfc = changeLogService.copy(group, new Group(), new String[] { "name", "description", "domain" });
				changeLogService.registerChangesWithDomain("group", "create", group.getId(), principal.getName(), null, null, clfc, Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);
			} catch (DataIntegrityViolationException e) {
				return Response.<lithium.service.user.data.entities.Group>builder().status(INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.<lithium.service.user.data.entities.Group>builder().status(INTERNAL_SERVER_ERROR).build();
		}
		return Response.<lithium.service.user.data.entities.Group>builder().data(group).status(OK).build();
	}
}
