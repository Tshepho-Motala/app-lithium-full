package lithium.service.cashier.client.internal;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoStateFieldGroup;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient
public interface InitialValidateClient {
    @RequestMapping(value = "/internal/initial-validate/{domainName}/{type}", method = RequestMethod.POST)
    public Response<Boolean> validate(@RequestBody Map<String, DoStateFieldGroup> inputFieldGroups, @PathVariable("domainName") String domainName, @PathVariable("type") String type) throws Exception;
}
