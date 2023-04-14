package lithium.service.accounting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;

@FeignClient(name="service-accounting-provider-internal", path="/player")
public interface AccountingPlayerClient {

	@RequestMapping("/{domainName}/findNetLossToHouse")
	public Response<Long> findNetLossToHouse(
			@PathVariable("domainName") String domainName, 
			@RequestParam("periodId") Long periodId, 
			@RequestParam("currency") String currency, 
			@RequestParam("playerGuid") String playerGuid) throws Exception;

	
}