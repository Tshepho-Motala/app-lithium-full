package lithium.service.cashier.mock.vespay.controllers;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.mock.vespay.data.objects.PurchaseRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v2/shop")
@Slf4j
public class ShopController {
	@Autowired LithiumConfigurationProperties config;
	
	@RequestMapping(value="/purchase", method=RequestMethod.GET)
	public ModelAndView makePurchase(
		WebRequest webRequest,
		@RequestParam("apikey") String apiKey,
		@RequestParam("traceid") String traceId
	) {
		log.info("apiKey = " + apiKey + ", traceId = " + traceId);
		return new ModelAndView("purchase", "pr", new PurchaseRequest(apiKey, traceId));
	}
	
	@RequestMapping(value="/purchasepost", method=RequestMethod.POST)
	public String makePurchaseAndSendCallbackPost(
		@RequestParam("apiKey") String apiKey,
		@RequestParam("traceId") String traceId,
		@RequestParam("amount") String amount,
		@RequestParam("cardNo") String cardNo,
		@RequestParam("month") String month,
		@RequestParam("year") String year,
		@RequestParam("cvc") String cvc,
		HttpServletResponse response
	) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			protected boolean hasError(HttpStatus statusCode) {
				response.setStatus(statusCode.value());
				return false;
			}
		});
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
		map.add("sender", "shop");
		map.add("apikey", apiKey);
		map.add("statuscode", "1");
		map.add("statusdescription", "");
		map.add("traceid", traceId);
		map.add("transactionid", traceId);
		map.add("orderid", traceId);
		map.add("billingdescriptor", "");
		map.add("bin", "");
		map.add("datetimecreated", "11/19/2018");
		map.add("timezone", "");
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		
		restTemplate.postForEntity(config.getGatewayPublicUrl() +
				"/service-cashier-processor-vespay/callback/130a21442bdc35f6bd53287775e10d49/", request, String.class);
		
		return "In the real world, this will now be a page containing the voucher code, with a copy function.";
	}
}
