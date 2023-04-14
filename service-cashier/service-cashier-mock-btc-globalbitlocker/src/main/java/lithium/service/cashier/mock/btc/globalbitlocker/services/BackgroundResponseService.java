package lithium.service.cashier.mock.btc.globalbitlocker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lithium.service.cashier.mock.btc.globalbitlocker.Configuration;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BackgroundResponseService {
	
	@Autowired Configuration config;
	
	@Retryable
	@Async
	public void postBackgroundResponse() {
		try { Thread.sleep(10000); } catch (InterruptedException ie) {};
		
//		RequestOrResponse<DepositResponse> bgRequest = new RequestOrResponse<>(secret) 
//		
//		RestTemplate rest = new RestTemplate();
//		ResponseEntity<Void> bgRequestResponse = rest.postForEntity(response.getBgReturnUrl(), response, String.class);
//		log.info("BackgroundResponse " + response + " result " + result);
	}

}
