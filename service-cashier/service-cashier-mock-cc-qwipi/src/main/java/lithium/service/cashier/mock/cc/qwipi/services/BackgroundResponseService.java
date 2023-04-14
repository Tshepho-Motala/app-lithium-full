package lithium.service.cashier.mock.cc.qwipi.services;

import java.lang.reflect.InvocationTargetException;

import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import lithium.service.cashier.processor.cc.qwipi.data.PaymentResponseS2S;
import lithium.util.ObjectToHttpEntity;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BackgroundResponseService {
	@Retryable
	@Async
	public void postBackgroundResponse(PaymentResponseS2S response) throws RestClientException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
//		try { Thread.sleep(10000); } catch (InterruptedException ie) {};
		RestTemplate rest = new RestTemplate();
		log.info("Sending to : "+response.getBgReturnUrl()+" :: "+response);
		String result = rest.postForObject(response.getBgReturnUrl(), ObjectToHttpEntity.forPostFormFormParam(response), String.class);
		log.info("BackgroundResponse " + response + " result " + result);
	}
}
