package lithium.service.cashier.client.internal;

import lithium.service.Response;
import lithium.service.cashier.client.objects.Bank;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

@FeignClient
public interface BanksLookupClient {
    @RequestMapping(path = "/system/banks", method = RequestMethod.POST)
    public Response<List<Bank>> banks(@RequestBody Map<String, String> processorProperties) throws Exception;
}