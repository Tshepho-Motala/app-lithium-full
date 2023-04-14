package lithium.service.sms.provider.clickatell;

import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.provider.DoProviderInterface;
import lithium.service.sms.provider.clickatell.data.ClickatellRequest;
import lithium.service.sms.provider.clickatell.data.ClickatellResponse;
import lithium.service.sms.provider.clickatell.data.ClickatellResponseData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DoProvider implements DoProviderInterface {
	@Override
	public DoProviderResponse send(DoProviderRequest request, RestTemplate restTemplate) throws Exception {
		DoProviderResponse response = new DoProviderResponse();
		response.setSmsId(request.getSmsId());
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.add("Authorization", request.getProperty("apiKey"));
		
		ClickatellRequest clickatellRequest = ClickatellRequest.builder()
			.clientMessageId(request.getSmsId().toString())
			.content(request.getContent())
			.to(request.getTo())
//			.userPriorityQueue(request.getPriority())
			.build();
		
		HttpEntity<ClickatellRequest> httpEntity = new HttpEntity<ClickatellRequest>(clickatellRequest, headers);
		
		try {
			ClickatellResponse clickatellResponse = restTemplate
				.postForObject(request.getProperty("baseUrl"), httpEntity, ClickatellResponse.class);
			
			log.info("Clickatell response for: " + clickatellRequest.toString() + " response: " + clickatellResponse.toString());
			if (clickatellResponse.getError() == null) {
				ClickatellResponseData responseData = clickatellResponse.getMessages().get(0);
				response.setProviderReference(responseData.getApiMessageId());
				if (responseData.isAccepted()) {
					response.setStatus(DoProviderResponseStatus.PENDING);
				} else {
					response.setStatus(DoProviderResponseStatus.FAILED);
					if (responseData.getError() != null) response.setMessage(responseData.getError());
				}
			} else {
				response.setStatus(DoProviderResponseStatus.FAILED);
				response.setMessage(clickatellResponse.getError());
			}
			
			return response;
		} catch (Exception ex) {
			log.error("Problem with clickatell sending of sms.", ex);
			throw ex;
		}
	}
}