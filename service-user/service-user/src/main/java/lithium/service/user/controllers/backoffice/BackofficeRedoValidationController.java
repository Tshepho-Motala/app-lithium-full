package lithium.service.user.controllers.backoffice;

import java.security.Principal;
import lithium.service.Response;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/backoffice/players/{domain}/{id}")
@Slf4j
public class BackofficeRedoValidationController {

  @Autowired private UserService userService;

  @PostMapping(value="/redo-email-validation")
  private Response<User> redoEmailValidation(
      @PathVariable("domain") String domain,
      @PathVariable("id") User user,
      @RequestParam(value="email", required=false) String email,
      LithiumTokenUtil util
  ) throws Exception {
    return userService.redoEmailValidation(
        domain,
        user,
        email,
        false,
        util);
  }

  @PostMapping(value="/v2/redo-email-validation")
  public Response<User> redoEmailValidationv2(
      @PathVariable("domain") String domain,
      @PathVariable("id") User user,
      @RequestParam(value="email", required=false) String email,
      LithiumTokenUtil util
  ) throws Exception {
    return userService.redoEmailValidation(
        domain,
        user,
        email,
        true,
        util);
  }

  @PostMapping(value="/redo-mobile-phone-validation")
  public Response<User> redoMobilePhoneValidation(
      @PathVariable("domain") String domain,
      @PathVariable("id") User user,
      @RequestParam(value="mobilephone", required=false) String mobilePhone,
      Principal principal
  ) {
    return userService.redoMobilePhoneValidation(domain, user, mobilePhone, principal);
  }
}
