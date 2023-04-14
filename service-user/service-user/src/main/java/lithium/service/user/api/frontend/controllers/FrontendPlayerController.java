package lithium.service.user.api.frontend.controllers;

import java.util.List;
import java.util.Locale;
import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.objects.VerificationStatus;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.TermsAndConditionsVersion;
import lithium.service.user.data.entities.User;
import lithium.service.user.exceptions.Status500InternalServerErrorException;
import lithium.service.user.services.PubSubUserService;
import lithium.service.user.services.UserService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/player")
public class FrontendPlayerController {
	@Autowired private UserService userService;
  @Autowired MessageSource messageSource;
  @Autowired ChangeLogService changeLogService;
  @Autowired PubSubUserService pubSubUserService;

	@GetMapping("/termsAndConditions")
	public TermsAndConditionsVersion termsAndConditions(LithiumTokenUtil tokenUtil)
			throws Status550ServiceDomainClientException {
		try {
		  return userService.userTermsAndConditionsVersion(tokenUtil.guid());
    } catch (Status550ServiceDomainClientException e) {
      throw new Status550ServiceDomainClientException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.getJwtUser().getDomainName())}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
    }
	}

	@PostMapping("/acceptTermsAndConditions")
	public TermsAndConditionsVersion acceptTermsAndConditions(LithiumTokenUtil tokenUtil, Locale locale)
		throws Status550ServiceDomainClientException,
			Status500InternalServerErrorException {
		try {
		  return userService.acceptTermsAndConditions(tokenUtil.domainName(), tokenUtil.guid(), tokenUtil.guid(),
          null, locale);
    } catch (Status550ServiceDomainClientException e) {
      throw new Status550ServiceDomainClientException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.getJwtUser().getDomainName())}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
    } catch (Status500InternalServerErrorException e) {
      throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.getJwtUser().getDomainName())}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
    }
	}

	@PostMapping("/opt-in-to-communications")
	public void optInToCommunications(@RequestParam("optIn") Boolean optIn, LithiumTokenUtil tokenUtil, Locale locale) {
		userService.optInToCommunications(optIn, tokenUtil);
	}

  @RequestMapping(path = "/profile/underage", method = RequestMethod.POST)
  public Long updateUnderageStatus(LithiumTokenUtil tokenUtil, @RequestParam("underage") boolean underage) throws Exception {
    User user = userService.findFromGuid(tokenUtil.guid());

    if (ObjectUtils.isEmpty(user)) {
      throw new UserNotFoundException();
    }

    if (user.getVerificationStatus().equals(VerificationStatus.UNDERAGED.getId()) && !underage) {
      user.setVerificationStatus(VerificationStatus.UNVERIFIED.getId());
      User updatedUser = userService.save(user);

      List<ChangeLogFieldChange> clfc = changeLogService.copy(updatedUser, user, new String[]{"verificationStatus"});
      changeLogService.registerChangesForNotesWithFullNameAndDomain("user", "edit", user.getId(), user.guid(),
          null, null, null, clfc, Category.ACCOUNT, SubCategory.ACCOUNT, 0, user.domainName());

      return updatedUser.getVerificationStatus();
    }

    return user.getVerificationStatus();
  }
}
