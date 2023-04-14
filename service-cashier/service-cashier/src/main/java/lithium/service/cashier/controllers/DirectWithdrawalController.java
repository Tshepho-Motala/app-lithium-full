package lithium.service.cashier.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.math.CurrencyAmount;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.frontend.DoResponse;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.objects.DirectWithdrawalTransaction;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.CashierService;
import lithium.service.cashier.services.DirectWithdrawalService;
import lithium.service.cashier.services.DomainMethodService;
import lithium.service.cashier.services.UserService;
import lithium.service.domain.client.objects.Domain;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cashier/direct-withdrawal")
@Slf4j
public class DirectWithdrawalController {

	@Autowired
	private DirectWithdrawalService directWithdrawalService;
	@Autowired
	private CashierService cashierService;
	@Autowired
	private UserService userService;
	@Autowired
	private DomainMethodService domainMethodService;

	private static final String DIRECT_WITHDRAWAL_PARAM_NAME = "direct_withdrawal_supported";

	@PostMapping
	public Response<Long> executeDirectWithdrawal(@RequestBody DirectWithdrawalTransaction request,
	                                              LithiumTokenUtil token, HttpServletRequest httpRequest) throws Exception {

		log.info("Direct withdrawal input request: " + request);

		String domainName = request.getDomainMethod().getDomain().getName();
		CurrencyAmount currencyAmount = CurrencyAmount.fromAmountString(request.getAmount());

        if (!directWithdrawalService.enoughFunds(domainName, currencyAmount, request.getBalanceLimitEscrow(), request.getUserGuid())) {
            return Response.<Long>builder()
                    .status(Status.INTERNAL_SERVER_ERROR)
                    .data2("Player does not have sufficient funds.")
                    .build();
        }

		String methodCode = request.getDomainMethod().getMethod().getCode();
		Map<String, String> fields = new HashMap<>();
		fields.put("processorAccountId", request.getProcessorAccountId());

		DoResponse response = directWithdrawalService.getDirectWithdrawalResponse(domainName, methodCode, request.getAmount(), fields,
				token.sessionId(), request.getUserGuid(), token.guid(), request.getBalanceLimitEscrow(), httpRequest.getRemoteAddr(),
				Collections.list(httpRequest.getHeaderNames()).stream()
						.collect(Collectors.toMap(header -> header, httpRequest::getHeader)), null);

		log.debug("response={}", response);
		if (response.getError() != null && response.getError()) {
			return Response.<Long>builder()
					.status(Status.INTERNAL_SERVER_ERROR)
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
					.status(Status.OK)
					.build();
		}
	}

	@GetMapping("/domain-methods")
	@JsonView(Views.Public.class)
	public Response<List<DomainMethod>> domainMethods(
			@RequestParam("domainName") String domainName,
			LithiumTokenUtil token,
			HttpServletRequest httpRequest
	) {

		List<DomainMethod> allowedDomainMethods = new ArrayList<>();
		try {
			log.info("domainName={}", domainName);
			allowedDomainMethods = domainMethodService.getAllowedDirectWithdrawMethods(
					DIRECT_WITHDRAWAL_PARAM_NAME,
					domainName,
					token.guid(),
					httpRequest.getRemoteAddr(),
					httpRequest.getHeader("User-Agent-Forwarded")
			);
		} catch (Exception ex) {
			log.error("Failed to get domain methods for domain =" + domainName, ex);
		}
		return Response.<List<DomainMethod>>builder().data(allowedDomainMethods).status(Status.OK).build();
	}

	@GetMapping("/get-player-balance")
	public Response<Long> getPlayerBalance(@RequestParam("domainName") String domainName, @RequestParam("userGuid") String userGuid) throws Exception {
		Domain domain = userService.retrieveDomainFromDomainService(domainName);
		Long playerBalance = cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(), userGuid);
		return Response.<Long>builder().data(playerBalance).status(Status.OK).build();
	}

	@GetMapping("/get-escrow-wallet-player-balance")
	public Response<Long> getEscrowWalletPlayerBalance(@RequestParam("domainName") String domainName, @RequestParam("userGuid") String userGuid) throws Exception {
		Domain domain = userService.retrieveDomainFromDomainService(domainName);
		Long escrowWalletPlayerBalance = cashierService.getCustomerBalance(domain.getCurrency(), domain.getName(), userGuid, "PLAYER_BALANCE_LIMIT_ESCROW", "PLAYER_BALANCE");
		return Response.<Long>builder().data(escrowWalletPlayerBalance).status(Status.OK).build();
	}
}
