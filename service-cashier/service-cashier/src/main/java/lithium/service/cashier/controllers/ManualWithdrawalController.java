package lithium.service.cashier.controllers;

import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.objects.ManualWithdrawalTransaction;
import lithium.service.cashier.exceptions.BalanceGetFailedException;
import lithium.service.cashier.services.CashierService;
import lithium.service.cashier.services.DirectWithdrawalService;
import lithium.service.cashier.services.UserService;
import lithium.service.domain.client.objects.Domain;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cashier/manual-withdrawal")
@Slf4j
public class ManualWithdrawalController {

	@Autowired
	private DirectWithdrawalService directWithdrawalService;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private UserService userService;

	@PostMapping
	public Response<Long> executeManualWithdrawal(@RequestBody ManualWithdrawalTransaction request,
	                                              LithiumTokenUtil token, HttpServletRequest httpRequest) throws Exception {

		log.debug("Manual withdrawal input request: " + request);

		String domainName = request.getDomainMethod().getDomain().getName();
		try {
			Domain domain = userService.retrieveDomainFromDomainService(domainName);
			CurrencyAmount amount = CurrencyAmount.fromAmountString(request.getAmount());
			long customerBalance = cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(), request.getUserGuid());
			if (customerBalance < amount.toCents()) {
				return Response.<Long>builder()
						.status(Response.Status.INTERNAL_SERVER_ERROR)
						.data2("Player does not have sufficient funds.")
						.build();
			}
		} catch (BalanceGetFailedException e) {
			throw new Exception("Could not get balance for " + request.getUserGuid() + " while trying to add a manual withdrawal transaction");
		}

		String methodCode = request.getDomainMethod().getMethod().getCode();
		Map<String, String> fields = new HashMap<>();
		fields.put("account_number", request.getAccountNumber());
		fields.put("bank_code", request.getBankCode());

		DoResponse response = directWithdrawalService.getDirectWithdrawalResponse(domainName, methodCode, request.getAmount(), fields,
				token.sessionId(), request.getUserGuid(), token.guid(), request.getBalanceLimitEscrow(), httpRequest.getRemoteAddr(),
				Collections.list(httpRequest.getHeaderNames()).stream()
						.collect(Collectors.toMap(header -> header, httpRequest::getHeader)), null);

		log.info("response={}", response);
		if (response.getError() != null && response.getError()) {
			return Response.<Long>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.data2(response.getErrorMessage())
					.build();
		} else {
			String userGuid = request.getUserGuid();
			String amount = request.getAmount();
			String domainMethodName = request.getDomainMethod().getName();
			String withdrawComment = request.getComment();
			directWithdrawalService.changeHistory(userGuid, amount, domainMethodName, token.guid(), withdrawComment, token);
			return Response.<Long>builder()
					.data(response.getTransactionId())
					.status(Response.Status.OK)
					.build();
		}
	}
}
