package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.objects.placeholders.CompletePlaceholdersClient;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.user.services.UserPlaceholderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashSet;
import java.util.Set;

@RestController
@Slf4j
public class UserPlaceholderController implements CompletePlaceholdersClient{

  @Autowired
  private UserPlaceholderService userPlaceholderService;

  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-guid")
  public Response<Set<Placeholder>> getPlaceholdersByGuid(String recipientGuid) {
    try {
      Set<Placeholder> placeholdersForGuid = userPlaceholderService.getPlaceholdersForGuid(recipientGuid);
      return Response.<Set<Placeholder>>builder()
          .data(placeholdersForGuid)
          .build();
    } catch (Exception e) {
      log.error("Can't get placeholders for guid:" + recipientGuid + " because of " + e.getMessage());
      return Response.<Set<Placeholder>>builder()
          .status(Status.INTERNAL_SERVER_ERROR)
          .message(e.getMessage())
          .build();
    }
  }

  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-transaction-id")
  public Response<Set<Placeholder>> getPlaceholdersByTransactionId(Long trId) {
    return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
  }

  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-domain-name")
  public Response<Set<Placeholder>> getPlaceholdersByDomainName(String domainName) {
    return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
  }
}
