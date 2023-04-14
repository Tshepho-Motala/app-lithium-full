package lithium.service.user.controllers;

import java.util.List;
import lithium.exceptions.Status401UnAuthorisedException;
import lithium.exceptions.Status470HashInvalidException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.user.data.entities.UserCategory;
import lithium.service.user.data.repositories.UserCategoryRepository;
import lithium.service.user.data.schema.PlayerTagRequest;
import lithium.service.user.services.DomainService;
import lithium.service.user.services.PublicApiAuthenticationService;
import lithium.service.user.services.UserCategoryService;
import lithium.service.user.services.UserEventService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/external/tags/v1")
@Slf4j
public class ExternalCategoryController {
	@Autowired UserEventService userEventService;

  @Autowired
  UserCategoryService userCategoryService;
  @Autowired
  DomainService domainService;
  @Autowired
  UserCategoryRepository userCategoryRepository;
  @Autowired
  UserService userService;

  @Autowired
  PublicApiAuthenticationService publicApiAuthenticationService;


  @PostMapping(value="/find-by-domain")
  public Response<List<UserCategory>> getPlayerTagListByDomain( @RequestBody PlayerTagRequest request) throws Status470HashInvalidException, Status401UnAuthorisedException, Status500InternalServerErrorException {
    publicApiAuthenticationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());
    return Response.<List<UserCategory>>builder().status(Response.Status.OK).data(userCategoryService.getPlayerTagListByDomain(request.getDomainName())).build();
  }

  @PostMapping(value="/list-player-tags")
  public Response<List<UserCategory>> getPlayerTagListByPlayerGuid(@RequestBody PlayerTagRequest request) throws Status470HashInvalidException, Status401UnAuthorisedException, Status500InternalServerErrorException {
    publicApiAuthenticationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());
      return Response.<List<UserCategory>>builder().status(Response.Status.OK).data(userCategoryService.getPlayerTagListByPlayerGuid(
          request.getPlayerGuid())).build();
  }

  @PostMapping(value="/add-player-tags")
  public Response<List<UserCategory>> setPlayerTagsByPlayerGuid(@RequestBody PlayerTagRequest request) throws Status470HashInvalidException, Status401UnAuthorisedException, Status500InternalServerErrorException{
    publicApiAuthenticationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());
    return Response.<List<UserCategory>>builder().status(Response.Status.OK).data(userCategoryService.setPlayerTagsByPlayerGuid(request.getPlayerGuid(), request.getTagIds())).build();
  }

  @DeleteMapping(value="/remove-player-tags")
  public Response<List<UserCategory>> removePlayerTagsByPlayerGuid(@RequestBody PlayerTagRequest request)
      throws Status470HashInvalidException, Status401UnAuthorisedException, Status500InternalServerErrorException {
    publicApiAuthenticationService.validate(request.getApiAuthorizationId(), request.payload(), request.getHash());
    return Response.<List<UserCategory>>builder().status(Response.Status.OK).data(userCategoryService.removePlayerTagsByPlayerGuid(
        request.getPlayerGuid(), request.getTagIds())).build();
  }
}
