package lithium.service.cashier.controllers.admin;

import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.objects.CheckBalanceResponse;
import lithium.service.cashier.machine.DoMachine;
import lithium.service.cashier.services.TransactionService;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/admin/{domain}/changestatus/{transactionId}")
@AllArgsConstructor
public class DoAdminController {
	
	private final WebApplicationContext beanContext;
	private final TransactionService transactionService;
	@Autowired LocaleContextProcessor localeContextProcessor;

	@RequestMapping("/approve")
	public DoResponse approve(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		@RequestParam(name="comment", required=false, defaultValue="Approve Transaction") String comment,
		HttpServletRequest httpServletRequest,
		@RequestParam(value = "locale", required = false) String locale
	) throws Exception {
		localeContextProcessor.setLocaleContextHolder(locale, token.domainName());
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.approve(domain, transactionId, comment, token);
	}

	@RequestMapping("/is-enough-balance")
	public Response<CheckBalanceResponse> isEnoughBalance(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		@RequestParam(name="currencyCode") String currencyCode,
		@RequestParam(name="guid") String guid,
		@RequestParam(name="isWithdrawalFundsReserved") boolean isWithdrawalFundsReserved
	) {
		boolean enoughBalance = transactionService.hasEnoughBalance(domain, guid, currencyCode, transactionId, isWithdrawalFundsReserved);
		return Response.<CheckBalanceResponse>builder().data(CheckBalanceResponse.builder()
						.enoughBalance(enoughBalance)
						.message(enoughBalance ? null : "Player does not have sufficient funds.")
						.build())
				.status(Response.Status.OK).build();
	}
	
	@RequestMapping("/success")
	public DoResponse success(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		@RequestParam(required=false) BigDecimal amount,
		@RequestParam(name="comment", required=false, defaultValue="Mark Success") String comment,
		HttpServletRequest httpServletRequest
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.markSuccess(domain, transactionId, (amount != null)? amount: null, comment, token);
	}
	
	@RequestMapping("/retry")
	public DoResponse retry(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		HttpServletRequest httpServletRequest
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.retry(domain, transactionId, token);
	}
	
	@RequestMapping("/cancel")
	public DoResponse cancel(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		HttpServletRequest httpServletRequest,
		@RequestParam(name="comment", required=false, defaultValue="Cancel Transaction") String comment
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.cancel(domain, transactionId, comment, token);
	}
	
	@RequestMapping("/clearProvider")
	public DoResponse clearProvider(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		@RequestParam(name="comment", required=false, defaultValue="Clear Provider") String comment,
		HttpServletRequest httpServletRequest
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.clearProvider(domain, transactionId, comment, token);
	}
	
	@RequestMapping("/reject")
	public DoResponse reject(
		LithiumTokenUtil token,
		@PathVariable Long transactionId,
		@PathVariable String domain,
		@RequestParam(name="comment", required=false, defaultValue="Reject Transaction") String comment,
		HttpServletRequest httpServletRequest
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return machine.reject(domain, transactionId, comment, token);
	}

	@RequestMapping("/on-hold")
	public Response<DoResponse> onHold(
			LithiumTokenUtil token,
			@PathVariable Long transactionId,
			@PathVariable String domain,
			@RequestParam String reason
	) throws Exception {
		DoMachine machine = beanContext.getBean(DoMachine.class);
		return Response.<DoResponse>builder().data(machine.onHold(domain, transactionId, reason, token)).status(Response.Status.OK).build();
	}

}
