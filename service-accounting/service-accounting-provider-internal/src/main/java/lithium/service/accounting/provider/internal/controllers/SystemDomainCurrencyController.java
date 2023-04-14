package lithium.service.accounting.provider.internal.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.accounting.objects.DomainCurrencyBasic;
import lithium.service.accounting.provider.internal.data.entities.DomainCurrency;
import lithium.service.accounting.provider.internal.services.DomainCurrencyService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/system/currencies")
@Slf4j
public class SystemDomainCurrencyController {
	@Autowired DomainCurrencyService service;
	
	@PostMapping("/syncDefaultCurrency")
	public Response<DomainCurrency> syncDefaultCurrency(
		@RequestParam("domainName") String domainName,
		@RequestParam("code") String code,
		@RequestParam("name") String name,
		@RequestParam("symbol") String symbol
	) {
		DomainCurrency domainCurrency = null;
		try {
			domainCurrency = service.syncDefaultCurrency(domainName,
				DomainCurrencyBasic.builder().code(code).name(name).symbol(symbol).build());
			return Response.<DomainCurrency>builder().data(domainCurrency).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<DomainCurrency>builder().data(domainCurrency).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}
