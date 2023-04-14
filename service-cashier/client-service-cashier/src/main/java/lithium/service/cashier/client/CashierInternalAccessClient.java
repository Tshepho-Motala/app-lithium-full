package lithium.service.cashier.client;

import lithium.service.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-cashier")
public interface CashierInternalAccessClient {
    @RequestMapping(path="/internal/check-authorisation", method= RequestMethod.GET)
    public Response<Boolean> isAuthorised(
            @RequestParam("domainName") String domainName,
            @RequestParam("methodCode") String methodCode,
            @RequestParam("ipAddress") String ipAddress,
            @RequestParam("userAgent") String userAgent,
            @RequestParam("userGuid") String userGuid,
            @RequestParam("deposit") Boolean deposit) throws Exception;
}
