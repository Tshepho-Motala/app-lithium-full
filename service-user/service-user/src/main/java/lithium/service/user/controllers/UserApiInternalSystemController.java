package lithium.service.user.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.UserApiInternalSystemClient;
import lithium.service.user.client.objects.Domain;
import lithium.service.user.client.objects.User;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserApiInternalSystemController implements UserApiInternalSystemClient {

  private final UserService userService;

  public UserApiInternalSystemController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/system/user-api-internal/user/create")
  public Response<lithium.service.user.client.objects.User> createStub(@RequestParam("domainName") String domainName,
      @RequestParam("userName") String userName) throws Exception {
    lithium.service.user.data.entities.User savedUser = userService.createUserStub(domainName, userName);
    lithium.service.user.data.entities.Domain savedDomain = savedUser.getDomain();

    Domain domain = Domain.builder().id(savedDomain.getId()).name(savedDomain.getName()).build();
    User userObject = User.builder().id(savedUser.getId()).guid(savedUser.getGuid()).domain(domain).build();

    return Response.<User>builder().data(userObject).status(Status.OK).build();
  }

}
