package lithium.service.cashier.client.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.cashier.client.objects.Limits;

@FeignClient(name="service-cashier")
public interface DoProcessorLimitsClients {
	@RequestMapping(path="/cashier/user/profile/limits", method=RequestMethod.GET) 
	public  Response<Limits> getUserDomainLimits(@RequestParam(name="userGuid") String userGuid);
}