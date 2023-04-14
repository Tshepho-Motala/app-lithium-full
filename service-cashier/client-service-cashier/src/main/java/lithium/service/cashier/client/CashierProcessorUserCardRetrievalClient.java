package lithium.service.cashier.client;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.cashier.client.objects.ProcessorAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="service-cashier")
public interface CashierProcessorUserCardRetrievalClient {
    @RequestMapping(path = "/system/user-payment-options/retrieve", method = RequestMethod.POST)
    List<ProcessorAccount> retrieveUserPaymentOptions(@RequestParam("domainName") String domainName,
        @RequestParam("userTokenId") String userTokenId) throws Status500InternalServerErrorException;
}
