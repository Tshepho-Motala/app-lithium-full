package lithium.service.cashier.controllers.frontend;

import com.fasterxml.jackson.annotation.JsonView;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.frontend.AddProcessorAccountRequest;
import lithium.service.cashier.client.frontend.ProcessorAccountResponse;
import lithium.service.cashier.client.objects.PaymentMethodStatusType;
import lithium.service.cashier.client.objects.ProcessorAccount;
import lithium.service.cashier.client.objects.UserCard;
import lithium.service.cashier.data.entities.DomainMethod;
import lithium.service.cashier.data.entities.Image;
import lithium.service.cashier.data.entities.ProcessorAccountTransaction;
import lithium.service.cashier.data.entities.Transaction;
import lithium.service.cashier.data.entities.frontend.TransactionFE;
import lithium.service.cashier.data.objects.ProtectionOfCustomerFunds;
import lithium.service.cashier.data.views.Views;
import lithium.service.cashier.services.CashierFrontendService;
import lithium.service.cashier.services.ProcessorAccountAddService;
import lithium.service.cashier.services.ProcessorAccountService;
import lithium.service.cashier.services.ProcessorAccountServiceOld;
import lithium.service.cashier.services.ProcessorAccountTransactionService;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/frontend")
public class CashierFrontendController {
	@Autowired
	private CachingDomainClientService cachingDomainClientService;
	@Autowired
	private CashierFrontendService cashierFrontendService;
	@Autowired
	private UserApiInternalClientService userApiInternalClientService;
	@Autowired
	private ProcessorAccountService processorAccountService;
	@Autowired
	private ProcessorAccountServiceOld processorAccountServiceOld;
	@Autowired
	private ProcessorAccountAddService processorAccountAddService;


	@GetMapping("/methods/d")
	@JsonView(Views.Public.class)
	public Response<?> methodsDeposit(HttpServletRequest request, LithiumTokenUtil user)
			throws Status550ServiceDomainClientException, UserNotFoundException, UserClientServiceFactoryException {
		log.debug("Lookup Deposit Methods for userName:"+user.username()+" domainName:"+user.domainName());
		
//		String username = "riaans1";
//		String domainName = "luckybetz";
		String username = user.username();
		String domainName = user.domainName();
		String ipAddr = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");
		
		List<?> domainMethods = cashierFrontendService.methodsDeposit(user.guid(), ipAddr, userAgent);

		User externalUser = userApiInternalClientService.getUserByGuid(user.guid());

		Map<String, Object> additionalData = new LinkedHashMap<>();
		ProtectionOfCustomerFunds protectionOfCustomerFunds = ProtectionOfCustomerFunds.builder()
		.enabled(cachingDomainClientService.isProtectionOfCustomerFundsEnabled(domainName))
		.currentDomainVersion(cachingDomainClientService.getCurrentDomainProtectionOfCustomerFundsVersion(domainName))
		.acceptedUserVersion(externalUser.getProtectionOfCustomerFundsVersion())
		.build();
		additionalData.put("protectionOfCustomerFunds", protectionOfCustomerFunds);
		
		return Response.<List<?>>builder().data(domainMethods).data2(additionalData).status(Status.OK).build();
	}
	@GetMapping("/methods/d/image")
	@JsonView(Views.Image.class)
	public Response<?> methodsDepositWithImage(HttpServletRequest request, LithiumTokenUtil user)
			throws Status550ServiceDomainClientException, UserNotFoundException, UserClientServiceFactoryException {
		return methodsDeposit(request, user);
	}
	
	@GetMapping("/methods/w")
	@JsonView(Views.Public.class)
	public Response<?> methodsWithdraw(HttpServletRequest request, LithiumTokenUtil user) {
		log.debug("Lookup Withdraw Methods for userName:"+user.username()+" domainName:"+user.domainName());
		
		String ipAddr = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");
		
		List<?> domainMethods = cashierFrontendService.methodsWithdraw(user.guid(), ipAddr, userAgent);
		
		return Response.<List<?>>builder().data(domainMethods).status(Status.OK).build();
	}
	@GetMapping("/methods/w/image")
	@JsonView(Views.Image.class)
	public Response<?> methodsWithdrawWithImage(HttpServletRequest request, LithiumTokenUtil user) {
		return methodsWithdraw(request, user);
	}
	
	@GetMapping("/dm/{domainMethodId}/processors")
	@JsonView(Views.Public.class)
	public Response<?> processors(
		HttpServletRequest request,
		@PathVariable("domainMethodId") DomainMethod domainMethod,
		LithiumTokenUtil user
	) {
		if (domainMethod == null) return Response.<List<?>>builder().status(Status.NOT_FOUND).build();
		log.debug("Lookup Processors for userName:"+user.username()+" domainName:"+user.domainName()+" dm:"+domainMethod);
//		String username = "riaans1";
//		String domainName = "luckybetz";
		String username = user.username();
		String domainName = user.domainName();
		String ipAddr = request.getRemoteAddr();
		String userAgent = request.getHeader("User-Agent");
		
		List<?> domainMethodProcessors = cashierFrontendService.domainMethodProcessors(domainMethod.getId(), user.guid(), ipAddr, userAgent);
//		List<?> domainMethods = cashierFrontendService.methodsWithdraw(username, domainName);
		
		return Response.<List<?>>builder().data(domainMethodProcessors).status(Status.OK).build();
	}
	
	@GetMapping("/dm/{domainMethodId}/image")
	public void domainMethodImage(
		@PathVariable("domainMethodId") DomainMethod domainMethod,
		HttpServletResponse response
	) throws IOException {
		Image image = (domainMethod.getImage()!=null)?domainMethod.getImage():domainMethod.getMethod().getImage();
		if (image != null) {
			OutputStream out = null;
			String mimeType = image.getFiletype();
			response.setContentType(mimeType);
			try {
				out = response.getOutputStream();
				out.write(image.getBase64());
				out.flush();
			} finally {
				out.close();
			}
		}
	}

	@PostMapping("/processor-account")
	public Response<ProcessorAccountResponse> addProcessorAccount(
			@RequestBody AddProcessorAccountRequest request,
			LithiumTokenUtil token,
			HttpServletRequest httpServletRequest
	) {
		try {
			ProcessorAccountResponse processorAccountResponse = processorAccountAddService.addProcessorAccount(request, token.domainName(), token.guid(),
					httpServletRequest.getRemoteAddr(), httpServletRequest.getHeader("user-agent"));
			return Response.<ProcessorAccountResponse>builder()
					.data(processorAccountResponse)
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed add processor account. Exception " + e.getMessage(), e);
			return Response.<ProcessorAccountResponse>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message("Failed add processor account for methodCode: " + request.getMethodCode())
					.build();
		}
	}

	@GetMapping("/processor-accounts")
	public Response<List<ProcessorAccount>> getProcessorAccounts(
			@RequestParam boolean isDeposit,
			LithiumTokenUtil token,
			HttpServletRequest httpServletRequest
	) {
		try {
			String ip = (httpServletRequest.getHeader("X-Forwarded-For") != null) ? httpServletRequest.getHeader("X-Forwarded-For") : httpServletRequest.getRemoteAddr();
			List<ProcessorAccount> response = processorAccountService.getActiveProcessorAccountsMethodsEnabled(token.guid(), ip, httpServletRequest.getHeader("user-agent"), isDeposit);
			return Response.<List<ProcessorAccount>>builder()
					.data(response)
					.status(Response.Status.OK)
					.build();
		} catch (Exception e) {
			log.error("Failed to get processor accounts for user: " + token.guid() +" isDeposit: " + isDeposit + ". Exception " + e.getMessage(), e);
			return Response.<List<ProcessorAccount>>builder()
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message("Failed to get processor accounts.")
					.build();
		}
	}

	@GetMapping("/processor-account/transaction/{transactionId}")
	@JsonView(Views.Public.class)
	public lithium.service.cashier.client.objects.ProccesorAccountTransaction getProcessorAccountTransaction(
			@PathVariable("transactionId") ProcessorAccountTransaction transaction,
			LithiumTokenUtil user
	) {
		return transaction != null && transaction.getUser().getGuid().equalsIgnoreCase(user.guid()) ? processorAccountAddService.getProcessorAccountTransaction(transaction) : null;
	}

	@GetMapping("/usedcards")
	public Response<List<UserCard>> getUserCards(
			@RequestParam String methodCode,
			@RequestParam boolean isDeposit,
			@RequestHeader(value = "User-Agent") String userAgent,
			LithiumTokenUtil token,
			HttpServletRequest request)
	{
		List<UserCard> userCards = null;
		try {
			userCards = processorAccountServiceOld.getUserCardsPerMethodCode(methodCode, true, token.username(), token.guid(), token.domainName(),
					request.getRemoteAddr(), userAgent);
			if (userCards != null && !userCards.isEmpty()) {
				userCards = userCards.stream().filter(uc -> uc.getStatus() == PaymentMethodStatusType.ACTIVE
						|| uc.getStatus() == PaymentMethodStatusType.DEPOSIT_ONLY && isDeposit
						|| uc.getStatus() == PaymentMethodStatusType.WITHDRAWAL_ONLY && !isDeposit).collect(Collectors.toList());
			}
		} catch (Exception e) {
			log.error("Failed to get user cards. Exception " + e.getMessage(), e);
			return Response.<List<UserCard>>builder()
					.data(userCards)
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.message("Failed to get user cards.")
					.build();
		}
		return Response.<List<UserCard>>builder()
				.data(userCards)
				.status(Response.Status.OK)
				.build();
	}
}
