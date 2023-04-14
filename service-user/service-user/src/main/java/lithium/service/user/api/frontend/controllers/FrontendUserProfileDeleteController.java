package lithium.service.user.api.frontend.controllers;

import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status404UserNotFoundException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.UserGuidStrategy;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.user.data.entities.User;
import lithium.service.user.services.UserProfileService;
import lithium.service.user.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.tokens.LithiumTokenUtil;

@Slf4j
@RestController
@RequestMapping("/frontend/profile")
public class FrontendUserProfileDeleteController {
  @Autowired
  private UserService userService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private LocaleContextProcessor localeContextProcessor;
  @Autowired
  private UserProfileService userProfileService;

  @DeleteMapping("/delete")
  public Response<String> removePlayerProfile(LithiumTokenUtil tokenUtil, @RequestParam(value = "locale", required = false) String locale) throws Exception {

    User user = userService.findFromGuid(tokenUtil.guid());

    localeContextProcessor.setLocaleContextHolder(locale, user.domainName());
    if (LithiumTokenUtil.getUserGuidStrategy() != UserGuidStrategy.ID) {
      throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PROFILE_DELETION_INVALID_GUID_STRATEGY",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())},
          LocaleContextHolder.getLocale()));
    }

    if (!userService.isDomainNameOfEcosystemRootType(user.domainName())) {
      throw new Status403AccessDeniedException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.PROFILE_DELETION_DENIED",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName()), user.domainName()},
          LocaleContextHolder.getLocale()));
    }

    if (user.isDeleted()) {
      throw new Status404UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND",
          new Object[]{new lithium.service.translate.client.objects.Domain(user.domainName())},
          LocaleContextHolder.getLocale()));
    }

    return Response.<String>builder()
        .data(userProfileService.deletePlayerAccount(user, tokenUtil))
        .status(Response.Status.OK)
        .build();
  }

}
