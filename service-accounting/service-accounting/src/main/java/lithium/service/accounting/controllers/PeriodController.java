package lithium.service.accounting.controllers;

import lithium.service.Response;
import lithium.service.accounting.exceptions.Status510AccountingProviderUnavailableException;
import lithium.service.accounting.objects.Period;
import lithium.service.accounting.service.AccountingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/period")
public class PeriodController {
	@Autowired AccountingService accountingService;

	@RequestMapping("/findbyoffset")
	public Response<Period> findByOffset(@RequestParam String domainName, @RequestParam int granularity,
	        @RequestParam int offset) throws Status510AccountingProviderUnavailableException {
		return accountingService.periodClient().findByOffset(domainName, granularity, offset);
	}
}
