package lithium.service.user.api.frontend.controllers;

import static lithium.service.Response.Status.OK;

import java.security.Principal;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lithium.client.changelog.ChangeLogService;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.service.Response;
import lithium.service.limit.client.LimitInternalSystemService;
import lithium.service.limit.client.exceptions.Status490SoftSelfExclusionException;
import lithium.service.limit.client.exceptions.Status491PermanentSelfExclusionException;
import lithium.service.limit.client.exceptions.Status496PlayerCoolingOffException;
import lithium.service.limit.client.exceptions.Status500LimitInternalSystemClientException;
import lithium.service.raf.client.objects.ReferralConversion;
import lithium.service.user.client.objects.AddressBasic;
import lithium.service.user.client.objects.FrontendPlayerBasic;
import lithium.service.user.client.objects.PasswordBasic;
import lithium.service.user.client.objects.PlayerBasic;
import lithium.service.user.controllers.PlayerController;
import lithium.service.user.controllers.PlayersController;
import lithium.service.user.controllers.UserController;
import lithium.service.user.data.entities.User;
import lithium.service.user.data.repositories.GroupRepository;
import lithium.service.user.exceptions.Status401UnAuthorisedUserException;
import lithium.service.user.exceptions.Status423InvalidPasswordException;
import lithium.service.user.exceptions.Status456NewPasswordsMismatchException;
import lithium.service.user.exceptions.Status457CurrentAndNewPasswordMatchException;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.EmailValidationService;
import lithium.service.user.services.LimitService;
import lithium.service.user.services.LoginEventService;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.ReferralService;
import lithium.service.user.services.UserProfileService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/profile") //FIXME: Should be changed to /frontend/profile - decision is not to impact the existing frontend endpoints as part of PLAT-3717 where we are breaking the link between backoffice and frontend using same controller due to security concerns - FE API's should have fine grained service endpoints whereas backoffice could have course grained endpoints.
public class FrontendUserProfileController {
	@Autowired GroupRepository groupRepository;
	@Autowired ChangeLogService changeLogService;
	@Autowired TokenStore tokenStore;
	@Autowired UserController userController;
	@Autowired PlayerController playerController;
	@Autowired PlayersController playersController;
	@Autowired EmailValidationService emailValidationService;
	@Autowired LimitService limitService;
	@Autowired UserService userService;
	@Autowired LoginEventService loginEventService;
	@Autowired ReferralService referralService;
	@Autowired LimitInternalSystemService limitInternalSystemService;
	@Autowired PubSubUserService pubSubUserService;
  @Autowired UserProfileService userProfileService;
  @Autowired MessageSource messageSource;

	@ExceptionHandler(ErrorCodeException.class)
	public Response handleErrorCodeException(ErrorCodeException ex, HttpServletResponse response) {
		log.warn("FrontendUserProfileController:ErrorCodeException " + ex.getCode() + " " + ex.getMessage());
		return Response.<String>builder().message(ex.getMessage()).status(Response.Status.fromId(ex.getCode())).build();
	}
	
	@GetMapping
	public Response get(Authentication authentication) throws Exception {
    return userProfileService.get(authentication);
	}

	@PostMapping("/v2")
	public Response savev2(@RequestBody @Valid FrontendPlayerBasic frontendPlayerBasic, BindingResult bindingResult, Principal principal) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		User user = userService.findOne(util.id());
		if (user == null) return Response.<User>builder().status(Response.Status.UNAUTHORIZED).build();

    //The following line and method (declarePlayerBasicAndOverrideNonNullableFieldsWithCurrentData) is to compensate for missing opt values
    //being passed into the frontendPlayerBasic and being overridden with default values for non-nullable fields
    //(like marketing preferences [Optouts]) in the userUpdate PlayerBasic
    PlayerBasic userUpdate = userService.declarePlayerBasicAndOverrideNonNullableFieldsWithCurrentData(frontendPlayerBasic, user);

    String[] nullPropertyNames = userService.getNullPropertyNames(frontendPlayerBasic);
    BeanUtils.copyProperties(frontendPlayerBasic, userUpdate, nullPropertyNames);
    PlayerBasic parentUpdate = userService.mapParentPromotionOptOuts(frontendPlayerBasic, user);
    return userProfileService.savev2(userUpdate, parentUpdate, bindingResult, principal);
	}

  @PostMapping(value = "/saveaddress")
  private Response<User> saveAddress(
      @RequestBody @Valid AddressBasic addressBasic,
      LithiumTokenUtil util
  ) throws Exception {
	  addressBasic.setUserId(util.id());
		return userProfileService.saveAddress(addressBasic, util);
	}
	
	@PostMapping(value="/changepassword")
	public Response<User> changePassword(LithiumTokenUtil util, @RequestBody String password) throws Exception {
		return userProfileService.changePassword(util, password);
	}

	@PostMapping(value="/add-referral-after-signup")
	public Response<ReferralConversion> addReferral(Principal principal, @RequestParam("referrerGuid") String referrerGuid) throws Exception {
		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, principal).build();
		User user = userService.findOne(util.id());
		if (user == null) {
			throw new Status401UnAuthorisedUserException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.UNAUTHORIZED_USER", new Object[]{new lithium.service.translate.client.objects.Domain(util.getJwtUser().getDomainName())}, "UNAUTHORIZED REQUEST", LocaleContextHolder.getLocale()));
		}
		return referralService.addReferralAfterSignUp(referrerGuid, user);

	}

	@PostMapping(value="/updatepassword")
	public Response<User> updatePassword(
		@RequestBody PasswordBasic passwordBasic,
		LithiumTokenUtil util
	) throws
      Status404UserNotFoundException,
			Status423InvalidPasswordException,
			Status456NewPasswordsMismatchException,
			Status457CurrentAndNewPasswordMatchException,
			Status500InternalServerErrorException,
			Status500LimitInternalSystemClientException,
			Status491PermanentSelfExclusionException,
			Status490SoftSelfExclusionException,
			Status496PlayerCoolingOffException {
		return Response.<User>builder().data(userService.changePassword(util.guid(), passwordBasic, util)).status(OK).build();
	}

}
