package lithium.service.accounting.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.accounting.objects.Period;

@FeignClient(name="service-accounting")
public interface AccountingPeriodClient {
	
	@RequestMapping("/period/findbyoffset")
	public Response<Period> findByOffset(
			@RequestParam("domainName") String domainName,
			@RequestParam("granularity") int granularity,
			@RequestParam("offset") int offset);

}