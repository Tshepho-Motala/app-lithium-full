package lithium.service.user.controllers.backoffice;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lithium.client.changelog.ChangeLogService;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.objects.BasicUserCategory;
import lithium.service.user.data.entities.Domain;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.entities.UserCategoryProjection;
import lithium.service.user.data.entities.UserProjection;
import lithium.service.user.exceptions.Status400BadRequestException;
import lithium.service.user.exceptions.Status409DuplicateTagNameExistException;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.UserCategoryService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/players/tag")
public class UserCategoryController{

	@Autowired UserCategoryService userCategoryService;
	@Autowired UserService userService;
	@Autowired DomainService domainService;
  @Autowired ChangeLogService changeLogService;
  @Autowired LithiumTokenUtilService tokenService;
  @Autowired ModelMapper modelMapper;

	@GetMapping(value = "/list")
	public Response<Set<UserCategoryProjection>> categoryList(
      LithiumTokenUtil tokenUtil,
      @RequestParam(name = "domainNames") String[] domainNames
  ) throws Exception {
    return Response.<Set<UserCategoryProjection>>builder().status(Status.OK_SUCCESS)
        .data(userCategoryService.getCategoryList(tokenUtil, domainNames)).build();
  }

  @GetMapping(value = "/table")
	public DataTableResponse<UserCategoryProjection> categoryTable(
      @RequestParam(name = "domainNames") String[] domainNames,
      DataTableRequest request
  ) throws Status550ServiceDomainClientException {
    return new DataTableResponse<>(request, userCategoryService.getCategoryTable(domainNames, request));
  }

  @GetMapping(value = "/view/{id}")
	public Response<UserCategoryProjection> categoryView(
		@PathVariable("id") Long userCategoryId,
		Principal principal
	) throws Exception {
		log.info("UserCategory :"+userCategoryId);
		return Response.<UserCategoryProjection>builder().status(Status.OK_SUCCESS).data(userCategoryService.getCategoryById(userCategoryId)).build();
	}
  @GetMapping(value = "/view/{id}/players/count")
  public Response<Long> categoryViewPlayersCount(
      @PathVariable("id") Long userCategoryId
  ) {
    return Response.<Long>builder().status(Status.OK_SUCCESS)
        .data(userCategoryService.countByUserCategories(userCategoryId)).build();
  }
	@GetMapping(value = "/viewplayer")
	public Response<List<UserCategory>> categoryViewPlayer(
		@RequestParam("guid") String playerGuid,
		Principal principal
	) throws Exception {
		log.info("UserTags for :"+playerGuid);
		User user = userService.findFromGuid(playerGuid);
		if (user!=null) {
			return Response.<List<UserCategory>>builder().status(Status.OK_SUCCESS).data(user.getUserCategories()).build();
		} else {
			return Response.<List<UserCategory>>builder().status(Status.NOT_FOUND).build();
		}
	}
	@GetMapping(value = "/view/{id}/players")
	public DataTableResponse<User> categoryViewPlayers(
		@PathVariable("id") long userCategoryId,
		DataTableRequest request,
		Principal principal
	) throws Exception {
    return new DataTableResponse<>(request, userCategoryService.getCategoryViewPlayers(userCategoryId, request));
  }

  @PostMapping("/addplayer")
	public Response<User> categoryAddPlayer(
		@RequestParam(name="username", required=true) String username,
		@RequestParam(name="tagId", required=true) UserCategory userCategory,
		Principal principal
	) throws Exception {
		log.debug("UserTag AddPlayer : " + username + " to : " + userCategory);
		User user = userService.findByDomainNameAndUsername(userCategory.getDomain().getName(), username);

		//Check if the category is already added to this user
    if(userCategoryService.checkUserCategories(user.getUserCategories(), userCategory)){
      return Response.<User>builder().status(Status.EXISTS).data(user).build();
    }

		return Response.<User>builder()
        .status(Status.OK_SUCCESS)
        .data(userCategoryService.categoryAddPlayer(user, Stream.of(userCategory).collect(Collectors.toList()), principal))
        .build();
	}

	@DeleteMapping("/removeplayer")
	public Response<User> categoryRemovePlayer(
		@RequestParam(name="username", required=true) String username,
		@RequestParam(name="tagId", required=true) UserCategory userCategory,
		Principal principal
	) throws Exception {
		log.debug("tagRemovePlayer : " + username + " from : " + userCategory);
		User user = userService.findByDomainNameAndUsername(userCategory.getDomain().getName(), username);

		return Response.<User>builder()
        .status(Status.OK_SUCCESS)
        .data(userCategoryService.categoryRemovePlayer(user, Stream.of(userCategory).collect(Collectors.toList()), principal))
        .build();
	}

	@PostMapping
	public Response<UserCategory> categoryAddUpdate(
		@RequestBody BasicUserCategory basicUserCategory,
		Principal principal
	) throws Status400BadRequestException, Status409DuplicateTagNameExistException, Status550ServiceDomainClientException {
    // ToDo: change entities and tables to UserTag
		log.debug("UserTag AddUpdate");
    Domain domain = domainService.findDomainByName(basicUserCategory.getDomainName());
    return userCategoryService.createOrEditTag(basicUserCategory,domain);
	}

	@DeleteMapping("/removetag")
	public Response<UserCategory> deleteCategory(
	    @RequestParam("id") UserCategory category,
      Principal principal
  ) throws Exception {
    userCategoryService.deleteCategory(category);
    return Response.<UserCategory>builder().status(Status.OK_SUCCESS).build();
  }

  @GetMapping(value = "/get-categories-from-ids")
  public Response<Set<UserCategoryProjection>> getCategoriesFromIds(
      LithiumTokenUtil tokenUtil,
      @RequestParam(name = "domainName") String domainName,
      @RequestParam(name = "categoryIds") Long[] categoryIds
  ) throws Exception {
    return Response.<Set<UserCategoryProjection>>builder().status(Status.OK_SUCCESS).data(userCategoryService.getCategoriesFromIds(tokenUtil, domainName, categoryIds)).build();
  }

}
