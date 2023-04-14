package lithium.service.user.controllers.system;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.data.entities.IncompleteUser;
import lithium.service.user.data.entities.IncompleteUserLabelValue;
import lithium.service.user.services.IncompleteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system/players/{domainName}/incompleteuser")
public class SystemIncompleteUserController {
  @Autowired private IncompleteUserService incompleteUserService;
  @Autowired private MessageSource messageSource;

  @GetMapping("/additionaldata")
  public Response<Map<String, String>> findIncompleteUserLabelValues(@PathVariable("domainName") String domainName, @RequestParam("email") String email) throws UserNotFoundException {
    IncompleteUser icu = incompleteUserService.findFromEmail(email);
    if (icu == null)
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder
          .getLocale()));

    HashMap<String, String> additionalData = new HashMap<>();
    for (IncompleteUserLabelValue incompleteUserLabelValue : icu.getIncompleteUserLabelValueList()) {
      additionalData.put(incompleteUserLabelValue.getLabelValue().getLabel().getName(), incompleteUserLabelValue.getLabelValue().getValue());
    }
    return Response.<Map<String, String>>builder().data(additionalData).status(Status.OK).build();
  }

  @PostMapping("/additionaldata")
  public Response<String> updateOrAddIncompleteUserLabelValues(@PathVariable("domainName") String domainName, @RequestParam("email") String email, @RequestBody Map<String, String> additionalData)
      throws UserNotFoundException {
    IncompleteUser icu = incompleteUserService.findFromEmail(email);
    if (icu == null)
      throw new UserNotFoundException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.USER_NOT_FOUND", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "User not found or invalid user guid.", LocaleContextHolder.getLocale()));

    incompleteUserService.addOrUpdateDomainSpecificUserLabelValues(icu, additionalData);
    return Response.<String>builder().data(Status.OK.name()).status(Status.OK).build();
  }
}
