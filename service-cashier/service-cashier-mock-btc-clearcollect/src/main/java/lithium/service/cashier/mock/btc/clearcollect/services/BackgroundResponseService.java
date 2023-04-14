package lithium.service.cashier.mock.btc.clearcollect.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.mock.btc.clearcollect.Configuration;
import lithium.service.cashier.processor.btc.clearcollect.data.DepositResponse;
import lithium.service.cashier.processor.btc.clearcollect.data.RequestOrResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BackgroundResponseService {
	
	@Autowired Configuration config;
	
	@Retryable
	@Async
	public void postBackgroundResponse(DepositResponse response) {
		try { Thread.sleep(10000); } catch (InterruptedException ie) {};
		
//		RequestOrResponse<DepositResponse> bgRequest = new RequestOrResponse<>(secret) 
//		
//		RestTemplate rest = new RestTemplate();
//		ResponseEntity<Void> bgRequestResponse = rest.postForEntity(response.getBgReturnUrl(), response, String.class);
//		log.info("BackgroundResponse " + response + " result " + result);
	}

}
