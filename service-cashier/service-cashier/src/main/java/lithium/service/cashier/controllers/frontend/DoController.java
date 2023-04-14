package lithium.service.cashier.controllers.frontend;

import javax.servlet.http.HttpServletRequest;

import lithium.service.cashier.client.frontend.DoMachineState;
import lithium.service.cashier.client.objects.TransactionType;
import lithium.service.cashier.services.TransactionService;
import lithium.service.domain.client.util.LocaleContextProcessor;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.frontend.DoRequest;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.machine.DoMachine;
import lithium.tokens.LithiumTokenUtil;

@RestController
@RequestMapping("/frontend/method/{domainMethod}/{type}")
public class DoController {
	
	@Autowired WebApplicationContext beanContext;
	@Autowired LocaleContextProcessor localeContextProcessor;
	@Autowired TransactionService transactionService;
	
	@RequestMapping("/do")
	public Response<DoResponse> request(
		@PathVariable DomainMethod domainMethod,
		@PathVariable String type,
		@RequestParam(defaultValue="false", name="mobile") String mobile,
		LithiumTokenUtil token,
		DoRequest request,
		HttpServletRequest httpServletRequest,
		@RequestParam(value = "locale", required = false) String locale
	) {
		localeContextProcessor.setLocaleContextHolder(locale, token.domainName());
		if (request.getMobile() == null) request.setMobile(mobile.contentEquals("false") ? false : true);
		DoMachine machine = beanContext.getBean(DoMachine.class);
		boolean isFirstDeposit = BooleanUtils.isTrue(domainMethod.getDeposit()) && transactionService.findFirstTransaction(token.guid(), TransactionType.DEPOSIT, DoMachineState.SUCCESS.name()) == null;
		return Response.<DoResponse>builder()
			.data(machine.run(domainMethod, token, request, type, httpServletRequest, isFirstDeposit))
			.status(Status.OK)
			.build();
	}
	
}
