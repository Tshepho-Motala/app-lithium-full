package lithium.service.access.provider.gamstop.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.access.client.gamstop.exceptions.Status424InvalidRequestException;
import lithium.service.access.client.gamstop.exceptions.Status512ExclusionCheckException;
import lithium.service.access.client.gamstop.objects.ExclusionRequest;
import lithium.service.access.client.gamstop.objects.ExclusionResult;
import lithium.service.access.provider.gamstop.data.enums.ExclusionType;
import lithium.service.access.provider.gamstop.data.objects.SelfExclusionResponse;
import lithium.service.user.client.objects.User;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Slf4j
@Builder
public class GamstopAdapter {
	private RestTemplate restTemplate;

	public SelfExclusionResponse checkExclusionRaw(
			String url,
			String apiKey,
			String firstName,
			String lastName,
			Integer dobDay,
			Integer dobMonth,
			Integer dobYear,
			String email,
			String postalCode,
			String mobileNumber
	) throws Status512ExclusionCheckException {
		String dateOfBirth = "";

		if (dobDay != null && dobMonth != null && dobYear != null) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dob = new GregorianCalendar(dobYear, dobMonth - 1, dobDay).getTime();
			dateOfBirth = df.format(dob);
		}

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("firstName",firstName);
		map.add("lastName",lastName);
		map.add("dateOfBirth", dateOfBirth);
		map.add("email",email);
		map.add("postcode",postalCode != null ? postalCode : "");
		map.add("mobile",mobileNumber != null ? mobileNumber : "");
		log.debug("Request {}", map);
		HttpHeaders headers = setHeaders(apiKey, MediaType.APPLICATION_FORM_URLENCODED.toString());
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map,headers);
		SelfExclusionResponse exclusionResponse = new SelfExclusionResponse();
		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
			log.debug("Response {}",responseEntity.getHeaders());
			if (responseEntity != null && responseEntity.getHeaders() != null) {
				List<String> exclusionTypeResponse = responseEntity.getHeaders().get("X-Exclusion");
				if (exclusionTypeResponse != null && !exclusionTypeResponse.isEmpty()) {
					exclusionResponse.setExclusionType(ExclusionType.valueOf(exclusionTypeResponse.get(0)));
				}
				List<String> xUniqueId = responseEntity.getHeaders().get("X-Unique-Id");
				if (xUniqueId != null && !xUniqueId.isEmpty()) {
					exclusionResponse.setXUniqueId(xUniqueId.get(0));
				}
			}
		} catch (Exception ex) {
			log.debug("Error checking exclusion for {}", StringUtils.join(map.entrySet(), ","));
			log.error("Error checking exclusion for {}", ex);
			throw new Status512ExclusionCheckException("Error checking exclusion.");
		}
		return exclusionResponse;
	}
	public SelfExclusionResponse checkExclusion(
		String url,
		User user,
		String apiKey
	) throws Status512ExclusionCheckException {
		String postalCode = null;
		if (user.getResidentialAddress() != null) {
			postalCode = user.getResidentialAddress().getPostalCode();
		}
		if ((postalCode == null || postalCode.trim().isEmpty()) && user.getPostalAddress() != null) {
			postalCode = user.getPostalAddress().getPostalCode();
		}
		return checkExclusionRaw(
				url, apiKey, user.getFirstName(), user.getLastName(),
				user.getDobDay(), user.getDobMonth(), user.getDobYear(),
				user.getEmail(), postalCode, user.getCellphoneNumber());
	}
	private HttpHeaders setHeaders(String apiKey, String mediaType) {
		log.debug(apiKey);
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-API-Key", apiKey);
		headers.add("Content-Type", mediaType);
		log.debug(String.valueOf(headers));
		return headers;
	}

	public List<ExclusionResult> checkBatchExclusion(String url, String apiKey, List<ExclusionRequest> requestData) throws Status424InvalidRequestException, Status512ExclusionCheckException {
		if (CollectionUtils.isEmpty(requestData)) {
			throw new Status424InvalidRequestException("Batch Request cannot be empty");
		}
		HttpHeaders headers = setHeaders(apiKey, MediaType.APPLICATION_JSON_VALUE);
		try {
			HttpEntity<List<ExclusionRequest>> entity = new HttpEntity<>(requestData,headers);
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
			log.debug("{}",responseEntity);
			if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.hasBody()) {
				ObjectMapper mapper = new ObjectMapper();
				List<ExclusionResult> exclusionResults = Arrays.asList(mapper.readValue(responseEntity.getBody(), ExclusionResult[].class));
				log.debug("Response {}",responseEntity.getHeaders());
				log.debug("{}",responseEntity.getBody());
				log.debug("ExclusionResults {}",exclusionResults);
				return exclusionResults;
			}

			//Getting only correlationId to log
			List<Map<String,String>> correlationIds = requestData.stream().map(excl -> new HashMap<String, String>(){{
				put("CorrelationId", excl.getCorrelationId());
			}}).collect(Collectors.toList());

			log.debug("Error checking exclusions for {}", requestData);

			if (!log.isDebugEnabled()) {
				log.error("Error checking exclusions for {}", correlationIds);
			}

			throw new Status424InvalidRequestException("Error checking exclusion");
		} catch (Exception ex) {
			log.debug("Error processing batch request {}", requestData);
			log.error("Error processing batch request.", ex);
			throw new Status512ExclusionCheckException(ex.getLocalizedMessage());
		}
	}

}
