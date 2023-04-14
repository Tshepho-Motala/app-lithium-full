package lithium.service.accounting.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.Response;
import lithium.service.accounting.objects.DomainCurrency;

@FeignClient(name="service-accounting-provider-internal")
public interface AccountingDomainCurrencyClient {
	@RequestMapping("/currencies/domain/{domainName}/list")
	public Response<List<DomainCurrency>> list(@PathVariable("domainName") String domainName);
	
	/**
	 * Used in svc-domain, if a new domain is added, or if a domain is modified and the currency is changed.
	 * 
	 * @param domainName
	 * @param domainCurrencyBasic
	 * @return
	 */
	@RequestMapping(value="/system/currencies/syncDefaultCurrency", method=RequestMethod.POST)
	public Response<DomainCurrency> syncDefaultCurrency(
		@RequestParam("domainName") String domainName,
		@RequestParam("code") String code,
		@RequestParam("name") String name,
		@RequestParam("symbol") String symbol);
}
