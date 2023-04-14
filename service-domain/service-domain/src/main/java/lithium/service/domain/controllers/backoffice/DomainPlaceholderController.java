package lithium.service.domain.controllers.backoffice;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.objects.placeholders.CompletePlaceholdersClient;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.domain.client.util.DomainToPlaceholderBinder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashSet;
import java.util.Set;

@RestController
@Slf4j
public class DomainPlaceholderController implements CompletePlaceholdersClient {

  @Autowired
  private CachingDomainClientService cachingDomainClientService;


  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-domain-name")
  public Response<Set<Placeholder>> getPlaceholdersByDomainName(String domainName) {
    try {
      Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      return Response.<Set<Placeholder>>builder()
          .data(new DomainToPlaceholderBinder(domain).completePlaceholders())
          .build();
    } catch (Status550ServiceDomainClientException e) {
      log.error("Can't build domain placeholder for domainName:" + domainName + " because of " + e.getMessage());
      return Response.<Set<Placeholder>>builder()
          .status(Status.INTERNAL_SERVER_ERROR)
          .message(e.getMessage())
          .build();
    }
  }

  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-guid")
  public Response<Set<Placeholder>> getPlaceholdersByGuid(String recipientGuid) {
    return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
  }

  @Override
  @RequestMapping(value = "/complete-placeholders/get-by-transaction-id")
  public Response<Set<Placeholder>> getPlaceholdersByTransactionId(Long transactionId) {
    return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
  }
}
