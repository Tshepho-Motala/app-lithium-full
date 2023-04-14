package lithium.service.limit.controllers.system;

import lithium.service.Response;
import lithium.service.client.objects.placeholders.CompletePlaceholdersClient;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.limit.services.LimitPlaceholderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class LimitPlaceholderController implements CompletePlaceholdersClient {

    @Autowired
    private LimitPlaceholderService limitPlaceholderService;

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-guid")
    public Response<Set<Placeholder>> getPlaceholdersByGuid(String recipientGuid) {
        return Response.<Set<Placeholder>>builder().data(limitPlaceholderService.findPlaceholdersByGuid(recipientGuid)).build();
    }

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-transaction-id")
    public Response<Set<Placeholder>> getPlaceholdersByTransactionId(Long transactionId) {
        return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
    }

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-domain-name")
    public Response<Set<Placeholder>> getPlaceholdersByDomainName(String domainName) {
        return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
    }
}
