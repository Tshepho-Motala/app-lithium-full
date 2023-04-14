package lithium.service.user.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.INVALID_DATA;
import static lithium.service.Response.Status.OK;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.client.objects.GrdCategorized;
import lithium.service.user.client.objects.GroupBasic;
import lithium.service.user.client.objects.RolesBasic;
import lithium.service.user.data.entities.Category;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.GRD;
import lithium.service.user.data.entities.Group;
import lithium.service.user.data.entities.Role;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.DomainRepository;
import lithium.service.user.data.repositories.GRDRepository;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.data.repositories.RoleRepository;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/domain/{domainName}/group/{groupId}")
public class GroupController {
	@Autowired
	private DomainRepository domainRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private GRDRepository grdRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private TokenStore tokenStore;
  @Autowired
  LithiumTokenUtilService tokenService;
	@Autowired
	private ChangeLogService changeLogService;

	@Autowired UserService userService;

	@GetMapping
	public Response<Group> view(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName
	) throws Exception {
		return Response.<Group>builder().data(group).data2(domainName).status(OK).build();
	}
	
	@PostMapping
	public Response<Group> save(
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
		if (repoDomain.isPresent()) {
			log.debug("Working with domain : "+repoDomain);
			try {
				lithium.service.user.data.entities.Group oldGroup = groupRepository.findOne(groupBasic.getId());
				group = lithium.service.user.data.entities.Group
						.builder()
						.id(groupBasic.getId())
						.name(groupBasic.getName())
						.description(groupBasic.getDescription())
						.domain(repoDomain.get())
						.enabled(true)
						.deleted(false)
						.build();
				
				List<ChangeLogFieldChange> clfc = changeLogService.copy(group, oldGroup, new String[] { "name", "description", "enabled", "deleted", "domain" });
				changeLogService.registerChangesWithDomain("group", "edit", group.getId(), principal.getName(), null, null, clfc, lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);
				
				group = groupRepository.save(group);
				
				log.debug("group : "+group);
			} catch (DataIntegrityViolationException e) {
				return Response.<lithium.service.user.data.entities.Group>builder().status(INTERNAL_SERVER_ERROR).build();
			}
		} else {
			return Response.<lithium.service.user.data.entities.Group>builder().status(INTERNAL_SERVER_ERROR).build();
		}
		return Response.<lithium.service.user.data.entities.Group>builder().data(group).status(OK).build();
	}
	
	@PostMapping("/enabled/{enabled}")
	public Response<Group> enabled(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		@PathVariable("enabled") Boolean enabled,
		Principal principal
	) throws Exception {
		boolean oldValue = group.getEnabled();
		group.setEnabled(enabled);
		
		List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
		ChangeLogFieldChange c = ChangeLogFieldChange.builder()
				.field("enabled")
				.fromValue(String.valueOf(oldValue))
				.toValue(String.valueOf(group.getEnabled()))
				.build();
		clfc.add(c);
		changeLogService.registerChangesWithDomain("group", enabled? "enable":"disable", group.getId(), principal.getName(), null, null, clfc, lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);
		
		group = groupRepository.save(group);
		
		return Response.<Group>builder().data(group).status(OK).build();
	}
	
	@PostMapping("/remove")
	public Response<Group> remove(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		Principal principal
	) throws Exception {
		group.setEnabled(false);
		group.setDeleted(true);
		group.setName(group.getName()+"-"+new Date().getTime());
		
		List<ChangeLogFieldChange> clfc = changeLogService.copy(group, new Group(), new String[] { "enabled", "deleted", "name" });
		changeLogService.registerChangesWithDomain("group", "delete", group.getId(), principal.getName(), null, null, clfc, lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 0, domainName);

		Group groupResult = groupRepository.save(group);

		// get users in group
    Iterable<User> findAllByGroups = userService.findAllByGroups(group);
    // loop through users and remove from group
    findAllByGroups.forEach(user -> {

      if (user.getGroups() == null) {
        user.setGroups(new ArrayList<Group>());
      }
      List<Group> oldGroups = new ArrayList<Group>();
      for (Group g: user.getGroups()) {
        oldGroups.add(g);
      }

      user.getGroups().removeIf(g -> (g.getId().compareTo(group.getId()) == 0));

      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("groups")
          .fromValue(oldGroups.toString())
          .toValue(user.getGroups().toString())
          .build();
      List<ChangeLogFieldChange> clfchange = new ArrayList<ChangeLogFieldChange>();
      clfchange.add(c);

      changeLogService.registerChangesForNotesWithFullNameAndDomain("user.group", "edit", user.getId(), tokenService.getUtil(principal).guid(), tokenService.getUtil(principal),
          null, null, clfchange, lithium.client.changelog.Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, user.domainName());

      userService.save(user);
    });
		
		return Response.<Group>builder().data(groupResult).status(OK).build();
	}
	
	@GetMapping("/grds")
	public Response<GrdCategorized> grds(
		@PathVariable("groupId") Long groupId,
		@PathVariable("domainName") String domainName
  ) throws Exception {
    List<GRD> grds = grdRepository.findByGroupIdOrderByRoleCategory(groupId);
		Map<Domain, Map<Category, List<GRD>>> grdMap = grds.stream().collect(
			Collectors.groupingBy(GRD::getDomain,
				Collectors.groupingBy(gr ->
					gr.getRole().getCategory(),
					Collectors.toList()
				)
			)
		);
		GrdCategorized grdCategorized = GrdCategorized.builder().build();
		grdMap.forEach((domain, categoryMap) -> {
			GrdCategorized.Domain d =
			GrdCategorized.Domain.builder()
				.id(domain.getId())
				.name(domain.getName())
				.build();
			categoryMap.entrySet().stream()
			.sorted(Map.Entry.<Category, List<GRD>>comparingByKey((c1, c2) -> c1.getName().compareTo(c2.getName())))
			.forEachOrdered(ccc -> {
				Category category = ccc.getKey();
				List<GRD> grdList = ccc.getValue();
				GrdCategorized.Category c =
					GrdCategorized.Category.builder()
					.id(category.getId())
					.name(category.getName())
					.description(category.getDescription())
					.build();
				grdList.stream().sorted((g1, g2) -> g1.getRole().getName().compareTo(g2.getRole().getName())).forEach(grd -> {
					GrdCategorized.GRD g =
						GrdCategorized.GRD.builder()
						.id(grd.getId())
						.categoryId(c.getId())
						.selfApplied(grd.getSelfApplied())
						.descending(grd.getDescending())
						.name(grd.getRole().getName())
						.role(grd.getRole().getRole())
						.description(grd.getRole().getDescription())
						.build();
					c.addGRD(g);
				});
				d.addCategory(c);
			});
			grdCategorized.addDomain(d);
		});
    return Response.<GrdCategorized>builder().data(grdCategorized).status(OK).build();
	}
	
	@PostMapping("/grd/{id}/change/{change}/{type}")
	public Response<?> grdUpdate(
		@PathVariable("groupId") Group group,
		@PathVariable("id") GRD grd,
		@PathVariable("change") boolean change,
		@PathVariable("type") String type,
		@PathVariable("domainName") String domainName,
    LithiumTokenUtil util
	) throws Exception {
    GRD oldGrd = new GRD();
    BeanUtils.copyProperties(grd, oldGrd);

    if (type.equalsIgnoreCase("s")) {
      grd.setSelfApplied(change);
    } else if (type.equalsIgnoreCase("d")) {
      grd.setDescending(change);
    } else {
      return Response.builder().status(INVALID_DATA).build();
    }
    String message = "The above field(s) changed on the role: " + grd.getRole().getName() + " (" + grd.getRole().getRole() + ")";
    List<ChangeLogFieldChange> clfc = changeLogService.copy(grd, oldGrd, new String[] {"selfApplied", "descending"});
    changeLogService.registerChangesWithDomain("grd", "edit", group.getId(), util.username(), message, null, clfc, lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 70, domainName);
    grdRepository.save(grd);
    return Response.builder().status(OK).build();
	}
	
	@PostMapping("/removeRole/{id}")
	public Response<?> removeRole(
		@PathVariable("groupId") Group group,
		@PathVariable("id") Long grdId,
		@PathVariable("domainName") String domainName,
      LithiumTokenUtil util
	) throws Exception {
		log.info("/removeRole/{"+grdId+"}");
    GRD oldGrd=grdRepository.findOne(grdId);
    GRD newGrd=new GRD();
    List<ChangeLogFieldChange> clfc = changeLogService.compare(newGrd, oldGrd, new String[] {"selfApplied", "descending"});
    String message = "Deleted the role named: " + oldGrd.getRole().getName() + " ("+oldGrd.getRole().getRole()+").";
    changeLogService.registerChangesWithDomain("grd", "delete",
        group.getId(), util.guid(), message, null, clfc,
        lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 70, domainName);
    grdRepository.deleteById(grdId);
		return Response.builder().status(OK).build();
	}
	
	@GetMapping("/users/table")
	public DataTableResponse<User> usersTable(
		DataTableRequest request,
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		Principal principal
	) throws Exception {
		return new DataTableResponse<>(request, userService.findAllByGroups(request.getPageRequest(), group));
	}
	
	@PostMapping("/users/add/{username}")
	public Response<?> userAdd(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		@PathVariable("username") String username,
		Principal principal
	) throws Exception {
		User user = userService.findByDomainNameAndUsername(domainName.toLowerCase(), username.toLowerCase());
		if (user != null) {
//			List<Group> groups = user.getGroups();
			
			List<Group> oldGroups = new ArrayList<Group>();
			for (Group g: user.getGroups()) {
				oldGroups.add(g);
			}
			
			user.getGroups().add(group);
			
			ChangeLogFieldChange c = ChangeLogFieldChange.builder()
					.field("groups")
					.fromValue(oldGroups.toString())
					.toValue(user.getGroups().toString())
					.build();
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			clfc.add(c);
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
          null, null, clfc, lithium.client.changelog.Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domainName);

			userService.save(user);
			
			return Response.builder().status(OK).build();
		} else {
			return Response.builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/users/remove/{userId}")
	public Response<?> userAdd(
		@PathVariable("groupId") Group group,
		@PathVariable("userId") Long userId,
		@PathVariable("domainName") String domainName,
		Principal principal
	) throws Exception {
		User user = userService.findOne(userId);
		if (user != null) {
			List<Group> oldGroups = new ArrayList<Group>();
			for (Group g: user.getGroups()) {
				oldGroups.add(g);
			}
			
			user.setGroups(user.getGroups().stream().filter(g -> g.getId() != group.getId()).collect(Collectors.toList()));

			ChangeLogFieldChange c = ChangeLogFieldChange.builder()
					.field("groups")
					.fromValue(oldGroups.toString())
					.toValue(user.getGroups().toString())
					.build();
			List<ChangeLogFieldChange> clfc = new ArrayList<>();
			clfc.add(c);
			changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), principal.getName(), tokenService.getUtil(principal),
          null, null, clfc, lithium.client.changelog.Category.ACCOUNT, SubCategory.EDIT_DETAILS, 0, domainName);

			userService.save(user);
			return Response.builder().status(OK).build();
		} else {
			return Response.builder().status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/roles/{searchDomainName}")
	public Response<List<Role>> roles(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		@PathVariable("searchDomainName") String searchDomainName,
		Principal principal
	) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		final List<String> rolesForThisGroup = new ArrayList<>();
		if (searchDomainName != null) {
			Domain searchDomain = domainRepository.findByName(searchDomainName);
			List<GRD> grdsByDomainAndGroup = grdRepository.findByDomainIdAndGroupId(searchDomain.getId(), group.getId());
			List<String> rolesByDomainAndGroup = grdsByDomainAndGroup.stream().map(g -> g.getRole().getRole()).collect(Collectors.toList());
			rolesForThisGroup.addAll(rolesByDomainAndGroup);
		}
		
		List<Role> finalRoleList = new ArrayList<>();
//		Comparator<Employee> byLastName = (e1, e2) -> e1.getEmployeeLastName().compareTo(e2.getEmployeeLastName());
//		employees.stream().sorted(byFirstName.thenComparing(byLastName))
		Comparator<Role> byRole = (r1, r2) -> r1.getRole().compareTo(r2.getRole());
		Comparator<Role> byCategory = (r1, r2) -> r1.getCategory().getName().compareTo(r2.getCategory().getName());
		
		if (util.hasAdminRole(searchDomainName)) {
			List<Role> allRoles = StreamSupport.stream(roleRepository.findAll().spliterator(), false)
			.filter(r -> {
				if (!rolesForThisGroup.contains(r.getRole())) {
					return true;
				}
				return false;
			})
			.sorted(byCategory.thenComparing(byRole))
			.collect(Collectors.toList());
			finalRoleList.addAll(allRoles);
		} else {
			List<String> domainRoles = util.roles(searchDomainName);
			if (domainRoles!=null) domainRoles.forEach(r -> {
				if (!rolesForThisGroup.contains(r)) {
					finalRoleList.add(roleRepository.findByRole(r));
				}
			});
			finalRoleList.sort((r1, r2) -> r1.getCategory().getId().compareTo(r2.getCategory().getId()));
		}
		
		return Response.<List<Role>>builder().data(finalRoleList).status(OK).build();
	}
	
	@PostMapping("/addrole/{grdDomain}")
	public Response<?> addRole(
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		@PathVariable("grdDomain") String grdDomain,
		@RequestBody RolesBasic[] rolesBasic,
      LithiumTokenUtil util
	) throws Exception {
		Domain domain = domainRepository.findByName(grdDomain);
		
		for (RolesBasic r:rolesBasic) {
			if (r.getSelf() || r.getChild()) {
				GRD grd = GRD.builder()
				.selfApplied(r.getSelf())
				.descending(r.getChild())
				.group(group)
				.domain(domain)
				.role(roleRepository.findByRole(r.getRole()))
				.build();
				grd = grdRepository.save(grd);
        List<ChangeLogFieldChange> clfc = changeLogService.copy(grd,new GRD(), new String[] {"selfApplied", "descending"});
        String message = "Added role with the name: " + grd.getRole().getName() + " ("+grd.getRole().getRole()+"). The field value(s) listed above is in effect.";
        changeLogService.registerChangesWithDomain("grd", "create", group.getId(), util.username(), message, null, clfc, lithium.client.changelog.Category.SUPPORT, SubCategory.SUPPORT, 70, domainName);
			}
		};
		return Response.builder().status(OK).build();
	}
	
	@GetMapping("/roles/dt/{searchDomainId}")
	public DataTableResponse<Role> rolesDt(
		DataTableRequest request,
		@PathVariable("groupId") Group group,
		@PathVariable("domainName") String domainName,
		@PathVariable("searchDomainId") Domain searchDomain,
		Principal principal
	) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		final List<String> grdRoles = new ArrayList<>();
		if (searchDomain != null) {
			List<GRD> grds = grdRepository.findByDomainIdAndGroupId(searchDomain.getId(), group.getId());
			List<String> grdRoles2 = grds.stream().map(g -> g.getRole().getRole()).collect(Collectors.toList());
			grdRoles.addAll(grdRoles2);
		}
		
		List<Role> roles = new ArrayList<>();
		List<String> domainRoles = util.roles(domainName);
		if (domainRoles!=null) domainRoles.forEach(r -> {
			if (!grdRoles.contains(r)) {
				roles.add(roleRepository.findByRole(r));
			}
		});
		
		return new DataTableResponse<Role>(request, roles);
	}
	
	@GetMapping("/users")
	public Response<Iterable<User>> users(
		@PathVariable("groupId") Group group,
		Principal principal
	) throws Exception {
		Iterable<User> findAllByGroups = userService.findAllByGroups(group);
		return Response.<Iterable<User>>builder().data(findAllByGroups).status(OK).build();
	}
	
	@GetMapping(value = "/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable Long groupId, @RequestParam int p) throws Exception {
    return changeLogService.listLimited(ChangeLogRequest.builder()
            .entityRecordId(groupId)
            .entities(new String[] { "group", "grd" })
            .page(p)
            .build());
	}
}
