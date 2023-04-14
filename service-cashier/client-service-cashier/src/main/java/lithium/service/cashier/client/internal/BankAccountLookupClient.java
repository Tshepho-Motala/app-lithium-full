package lithium.service.cashier.client.internal;

import lithium.service.Response;
import lithium.service.cashier.client.objects.BankAccountLookupResponse;
import lithium.service.cashier.client.objects.BankAccountLookupRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient
public interface BankAccountLookupClient {
    @RequestMapping(path = "/system/bank-account-lookup", method = RequestMethod.POST)
    public Response<BankAccountLookupResponse> bankAccountLookup(@RequestBody BankAccountLookupRequest bankAccountLookupRequest) throws Exception;
}
