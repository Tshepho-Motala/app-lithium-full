package lithium.service.cashier.controllers.backoffice;

import lithium.service.Response;
import lithium.service.cashier.services.CashierPlaceholderService;
import lithium.service.client.objects.placeholders.CompletePlaceholdersClient;
import lithium.service.client.objects.placeholders.Placeholder;
import lithium.service.exception.ResponseErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
public class CashierPlaceholderController implements CompletePlaceholdersClient {

    @Autowired
    private CashierPlaceholderService cashierPlaceholderService;

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-transaction-id")
    public Response<Set<Placeholder>> getPlaceholdersByTransactionId(Long transactionId) {
        try {
            return Response.<Set<Placeholder>>builder()
                    .data(cashierPlaceholderService.buildForTransactionId(transactionId))
                    .build();
        } catch (ResponseErrorException e) {
            return Response.<Set<Placeholder>>builder()
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getFrontendMessage())
                    .build();
        }
    }

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-guid")
    public Response<Set<Placeholder>> getPlaceholdersByGuid(String recipientGuid) {
        return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
    }

    @Override
    @RequestMapping(value = "/complete-placeholders/get-by-domain-name")
    public Response<Set<Placeholder>> getPlaceholdersByDomainName(String domainName) {
        return Response.<Set<Placeholder>>builder().data(new HashSet<>()).build();
    }
}
