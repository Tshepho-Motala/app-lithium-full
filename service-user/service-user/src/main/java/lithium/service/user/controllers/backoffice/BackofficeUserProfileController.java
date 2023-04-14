package lithium.service.user.controllers.backoffice;

import lithium.exceptions.ErrorCodeException;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserProfileService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/backoffice/profile")
public class BackofficeUserProfileController {

  @Autowired UserProfileService userProfileService;

  @ExceptionHandler(ErrorCodeException.class)
  public Response handleErrorCodeException(ErrorCodeException ex, HttpServletResponse response) {
    log.warn("BackofficeUserProfileController:ErrorCodeException " + ex.getCode() + " " + ex.getMessage());
    return Response.<String>builder().message(ex.getMessage()).status(Response.Status.fromId(ex.getCode())).build();
  }

  @GetMapping
  public Response get(Authentication authentication) throws Exception {
    return userProfileService.get(authentication);
  }

  @PostMapping
  public Response<User> save(@RequestBody @Valid PlayerBasic userUpdate, LithiumTokenUtil util) throws Exception {
    return Response.<User>builder().data(userProfileService.updateAdminProfile(userUpdate, util)).build();
  }

  @PostMapping(value = "/save-address")
  private Response<User> saveAddress(
      @RequestBody @Valid AddressBasic addressBasic,
      LithiumTokenUtil util
  ) throws Exception {
    return userProfileService.saveAddress(addressBasic, util);
  }

  @PostMapping(value="/change-password")
  public Response<User> changePassword(LithiumTokenUtil util, @RequestBody String password) throws Exception {
    return userProfileService.changePassword(util, password);
  }

  @PostMapping(value = "/migrate-deleted-accounts")
  public Response<Integer> migrateDeletedAccounts(LithiumTokenUtil util) throws Exception {
    // FIXME: 2022/10/06 LSPLAT-6954 To remove migration
    return Response.<Integer>builder().status(Status.OK).data(userProfileService.migrateDeletedAccounts(util)).build();
  }
}
