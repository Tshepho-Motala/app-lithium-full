package lithium.service.cashier.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.cashier.client.CashierClient;
import lithium.service.cashier.client.objects.CashierTranType;
import lithium.service.cashier.client.objects.TransferRequest;
import lithium.service.cashier.client.objects.User;
import lithium.service.cashier.config.ServiceCashierConfigurationProperties;
import lithium.service.cashier.data.entities.Fees;
import lithium.service.cashier.services.CashierService;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.provider.ProviderConfig.ProviderType;
import lithium.service.domain.client.objects.Provider;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableConfigurationProperties(ServiceCashierConfigurationProperties.class)
@RestController
@RequestMapping("/cashier")
// no tight coupling between client and service (so no implementation or
// dependence on service-client
public class CashierController {

	@Autowired
	private LithiumServiceClientFactory services;
//	@Autowired
//	private ModelMapper mapper;
	@Autowired
	private TokenStore tokenStore;
	@Autowired
	private CashierService cashierService;
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@RequestMapping("/getUserInfo")
	public Response<User> getUserInfo(@RequestParam("guid") String guid, @RequestParam("apiToken") String apiToken, @RequestParam("currency") String currency)  throws Exception {
		//Get customer info and balance
		User user = cashierService.getUser(guid, apiToken, currency);
		if(user != null) {
			return Response.<User>builder().data(user).status(Status.OK).build();
		}
		
		return Response.<User>builder().status(Status.INVALID_DATA).build();
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@RequestMapping("/transfer")
	@Transactional
	public Response<Long> transfer(@RequestBody TransferRequest transfer)  throws Exception {
		//perform a transfer 
		//TODO: add bonus allocation in here
		//TODO: Store meta info for transaction in service-db
		//TODO: Store accounting data to DB
		//The userid is already the guid (domain/username)
		String userGuid = transfer.getUserName();
		Long tranId = 0L;
		if (transfer.getTransactionType().contentEquals(CashierTranType.DEPOSIT.toString())) {
			Response<Long> response = cashierService.processDeposit(
				transfer.getTransferCents(), 
				transfer.getDomainName(),
				userGuid,
				transfer.getTransactionId()+"", 
				transfer.getProviderGuid(),
				transfer.getTransactionMethod(),
				transfer.getCurrency(),
				Fees.builder().build(),
				null
			);
			tranId = response.getData();
		} else {
			Response<Long> response  = cashierService.processPayout(
				transfer.getTransferCents(), 
				transfer.getDomainName(),
				userGuid,
				transfer.getTransactionId()+"", 
				transfer.getProviderGuid(),
				transfer.getTransactionMethod(),
				transfer.getCurrency(),
				Fees.builder().build(),
				null
			);
			// More super dodgy code
			tranId = response.getData();
		}
		return Response.<Long>builder().data(tranId).status(Status.OK).build();
	}
	
	@Retryable(backoff=@Backoff(delay=500),maxAttempts=10)
	@RequestMapping("/startCashier")
	public RedirectView startCashier(@RequestParam("token") String token) throws Exception {

		LithiumTokenUtil util = LithiumTokenUtil.builder(tokenStore, token).build();
//		String guid = util.getJwtUser().getDomainName()+"/"+util.getJwtUser().getUsername();
		
		Iterable<Provider> providerList = cashierService.getProviderService().listByDomainAndType(util.getJwtUser().getDomainName(), ProviderType.CASHIER.type()).getData();
		if (providerList != null) {
			log.info("providerList :: "+providerList);
			Provider p = providerList.iterator().next();
			log.info("Using provider : "+p);
			CashierClient cc = services.target(CashierClient.class, p.getUrl(), true);
			log.debug("CashierClient : "+cc);
			String url = cc.startCashier(util.getJwtUser().getUsername(), util.getJwtUser().getDomainName(), util.getJwtUser().getApiToken()).getData();
			log.info("url : "+url);
			return new RedirectView(url);
		} else {
			log.error("No providers configured for domain: " + util.getJwtUser().getDomainName() +" with type: "+ ProviderType.CASHIER.type());
		}
		return null;
	}
}
