package lithium.service.cashier.client;

import lithium.service.Response;
import lithium.service.cashier.client.objects.TransferRequest;
import lithium.service.cashier.client.objects.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="service-cashier")
public interface CashierClient {
	
	@RequestMapping("/cashier/getUserInfo")
	public Response<User> getUserInfo(@RequestParam("guid") String guid, @RequestParam("apiToken") String apiToken, @RequestParam("currency") String currency)  throws Exception;
	
	@RequestMapping("/cashier/transfer")
	public Response<Long> transfer(@RequestBody TransferRequest transfer)  throws Exception;
	
	@RequestMapping("/cashier/startCashier")
	public Response<String> startCashier(
		@RequestParam("userName") String userName,
		@RequestParam("domainName") String domainName,
		@RequestParam("apiToken") String apiToken
	) throws Exception;
}

//Leaving out for round 1
//4. Get Bonus Settings
//Request URL: http://XXXX/
//Request Method: POST
//Type: Request, Response
//5. Personal Information Updated
//Request URL: http://XXXX/
//Request Method: POST
//Type: Request, Response
//6. Card Registered
//Request URL: http://XXXX/
//Request Method: POST
//Type: Request, Response
//7. Ewallet Account Registered
//Request URL: http://XXXX/
//Request Method: POST
//Type: Request, Response