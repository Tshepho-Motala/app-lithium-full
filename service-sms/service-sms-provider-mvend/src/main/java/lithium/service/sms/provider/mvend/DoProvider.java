package lithium.service.sms.provider.mvend;

import lithium.service.sms.client.internal.DoProviderRequest;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.provider.DoProviderInterface;
import lithium.service.sms.provider.mvend.data.Message;
import lithium.service.sms.provider.mvend.data.MvendRequest;
import lithium.service.sms.provider.mvend.data.MvendResponse;
import lithium.util.Hash;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class DoProvider implements DoProviderInterface {

	@Override
	public DoProviderResponse send(
		DoProviderRequest request,
		RestTemplate restTemplate
	) throws Exception {
		DoProviderResponse response = new DoProviderResponse();
		response.setSmsId(request.getSmsId());
		
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add("Content-Type", MediaType.APPLICATION_XML_VALUE);
		headers.add("Accept", MediaType.APPLICATION_XML_VALUE);
		headers.add("Authorization", request.getProperty("apiKey"));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String timestamp = LocalDateTime.now().format(formatter);
		String to = request.getTo().get(0);

		MvendRequest mvendRequest = MvendRequest.builder()
			.hash(
				Hash.builder(
					request.getProperty("apiKey"),
					":",
					request.getProperty("senderid"),
					to,
					request.getContent(),
					timestamp
				).hmacSha256()
			)
			.timestamp(timestamp)
			.message(
				Message.builder()
				.from(request.getProperty("senderid"))
				.to(to)
				.text(request.getContent())
				.build())
			.build();
		log.debug("Sending : "+mvendRequest+" to : "+request.getProperty("baseUrl"));

		HttpEntity<MvendRequest> httpEntity = new HttpEntity<MvendRequest>(mvendRequest, headers);
		
		try {
			MvendResponse mvendResponse = restTemplate.postForObject(request.getProperty("baseUrl"), httpEntity, MvendResponse.class);
			
			log.info("Mvend response for: " + mvendRequest.toString() + " response: " + mvendResponse.toString());
			if (mvendResponse.getResponse().getCode() != 0) {
				response.setProviderReference(mvendResponse.getResponse().getMessage());
				response.setStatus(DoProviderResponseStatus.FAILED);
			} else {
				response.setStatus(DoProviderResponseStatus.SUCCESS);
				response.setMessage(mvendResponse.getResponse().getMessage());
			}
			return response;
		} catch (Exception ex) {
			log.error("Problem with mvend sending of sms.", ex);
			throw ex;
		}
	}
}