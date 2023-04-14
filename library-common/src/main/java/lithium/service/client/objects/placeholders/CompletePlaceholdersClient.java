package lithium.service.client.objects.placeholders;

import lithium.service.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient
public interface CompletePlaceholdersClient {
    @RequestMapping(value = "/complete-placeholders/get-by-guid")
    Response<Set<Placeholder>> getPlaceholdersByGuid(@RequestParam(name = "recipientGuid") String recipientGuid);

    @RequestMapping(value = "/complete-placeholders/get-by-transaction-id")
    Response<Set<Placeholder>> getPlaceholdersByTransactionId(@RequestParam(name = "transactionId") Long transactionId);

    @RequestMapping(value = "/complete-placeholders/get-by-domain-name")
    Response<Set<Placeholder>> getPlaceholdersByDomainName(@RequestParam(name = "domainName") String domainName);
}
