package lithium.service.exchange.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lithium.service.Response;
import lithium.service.exchange.client.objects.ExchangeRequest;
import lithium.service.exchange.client.objects.ExchangeResponse;

@FeignClient("service-exchange")
public interface ExchangeClient {
	
	@RequestMapping("/exchange/rate/{fromCurrencyCode}/{toCurrencyCode}")
	public Response<Double> rate(
		@PathVariable("fromCurrencyCode") String fromCurrencyCode,
		@PathVariable("toCurrencyCode") String toCurrencyCode
	);
	
	@RequestMapping("/exchange")
	public Response<ExchangeResponse> exchange(
		@RequestBody ExchangeRequest request
	);
	
}