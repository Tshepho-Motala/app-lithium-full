package lithium.service.casino.provider.sportsbook.services;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.rest.EnableRestTemplate;
import lithium.rest.LoggingRequestInterceptor;
import lithium.service.casino.client.objects.TransactionDetailPayload;
import lithium.service.casino.exceptions.Status422InvalidParameterProvidedException;
import lithium.service.casino.exceptions.Status512ProviderNotConfiguredException;
import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.config.ProviderConfigService;
import lithium.service.casino.provider.sportsbook.data.Bet;
import lithium.service.casino.provider.sportsbook.request.BetInfoRequest;
import lithium.service.casino.provider.sportsbook.response.BetInfoResponse;
import lithium.util.HmacSha256HashCalculator;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static lithium.service.casino.CasinoTranType.SPORTS_BET;
import static lithium.service.casino.CasinoTranType.SPORTS_FREE_BET;

@Slf4j
@Service
@EnableRestTemplate
public class CasinoTransactionDetailService {
	private ProviderConfigService providerConfigService;
	private RestTemplate restTemplate;

	@Autowired
	public CasinoTransactionDetailService(@Qualifier("lithium.rest") RestTemplateBuilder builder,
										  ProviderConfigService providerConfigService) {
		this.restTemplate = builder
			.build();
		this.providerConfigService = providerConfigService;
	}

	public List<TransactionDetailPayload> handleTransactionDetailRequest(List<TransactionDetailPayload> transactionDetailRequestList
	) throws
			Status422InvalidParameterProvidedException,
			Status512ProviderNotConfiguredException {

		if (transactionDetailRequestList.size() > 0) {
			//Config
			ProviderConfig properties = providerConfigService
					.getConfig(transactionDetailRequestList.get(0).getProviderFromProviderGuid(),
							transactionDetailRequestList.get(0).getDomainFromProviderGuid());
			//Populate bets
			ArrayList<Bet> bets = new ArrayList<>(transactionDetailRequestList.size());
			transactionDetailRequestList.forEach(payLoad -> {
				if(SPORTS_FREE_BET.value().equalsIgnoreCase(payLoad.getTransactionType())
						|| SPORTS_BET.value().equalsIgnoreCase(payLoad.getTransactionType())) {
					bets.add(Bet.builder()
							.betId(payLoad.getProviderTransactionGuid().split(":")[0])
							.build());
				}
			});

			//Mould bets and request info
			BetInfoRequest betInfoRequest = constructBetInfoRequest(bets, properties);
			BetInfoResponse betInfoResponse = requestExternalBetInfo(betInfoRequest, properties);

			//Map output from response
			if (betInfoResponse != null) {
				transactionDetailRequestList.forEach(payload -> {
					betInfoResponse.getBetInfos().stream()
						.filter(betInfo -> betInfo.getBetId() != null)
						.filter(betInfo -> betInfo.getBetId().contentEquals(payload.getProviderTransactionGuid()))
						.findFirst()
						.ifPresent(betInfo -> {
							payload.setTransactionDetailUrl(betInfo.getUrl());
						});
				});
			}
		}
		return transactionDetailRequestList;
	}

	public TransactionDetailPayload handleTransactionDetailRequest(TransactionDetailPayload transactionDetailRequest
	) throws
			Status422InvalidParameterProvidedException,
			Status512ProviderNotConfiguredException {
		ProviderConfig properties = providerConfigService
				.getConfig(transactionDetailRequest.getProviderFromProviderGuid(),
						transactionDetailRequest.getDomainFromProviderGuid());

		BetInfoRequest betInfoRequest = constructBetInfoRequest(
				Bet.builder()
					.betId(transactionDetailRequest.getProviderTransactionGuid().split(":")[0])
					.build()
				, properties);

		BetInfoResponse betInfoResponse = requestExternalBetInfo(betInfoRequest, properties);
		if ((betInfoResponse != null) && (betInfoResponse.getBetInfos().size() > 0)) {

			transactionDetailRequest.setTransactionDetailUrl(betInfoResponse.getBetInfos().get(0).getUrl());
		}
		return transactionDetailRequest;
	}

	/**
	 * Constructs a hash using the bet ids and timestamp
	 * @param request
	 * @param preSharedKey
	 */
	private void generateSha256(BetInfoRequest request, String preSharedKey) {
		HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);

		request.getBets().forEach(bet ->{
			hasher.addItem(bet.getBetId());
		});
		hasher.addItem(request.getTimestamp());
		request.setSha256(hasher.calculateHash());
	}

	private BetInfoRequest constructBetInfoRequest(Bet bet, ProviderConfig properties) {
		ArrayList<Bet> betlist = new ArrayList<>(1);
		betlist.add(bet);
		return constructBetInfoRequest(betlist, properties);
	}

	private BetInfoRequest constructBetInfoRequest(ArrayList<Bet> betList, ProviderConfig properties) {
		BetInfoRequest betInfoRequest = BetInfoRequest.builder()
				.bets(betList)
				.timestamp(DateTime.now().getMillis())
				.build();
		generateSha256(betInfoRequest, properties.getHashPassword());
		return betInfoRequest;
	}

	private BetInfoResponse requestExternalBetInfo(BetInfoRequest betInfoRequest, ProviderConfig properties) {
		if (!StringUtil.isEmpty(properties.getExternalTransactionInfoUrl())) {
			if (betInfoRequest == null || ObjectUtils.isEmpty(betInfoRequest.getBets())) {
				log.warn("requestExternalBetInfo - 'betInfoRequest' parameter is null or contains no bets");
				return null;
			}

			final ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			String requestBody = null;
			try {
				requestBody = objectMapper.writeValueAsString(betInfoRequest);
			} catch (IOException e) {
				log.error("requestExternalBetInfo - Unable to convert request Json: " + betInfoRequest.toString(), e);
				return null;
			}

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(
					Arrays.asList(
							MediaType.APPLICATION_JSON,
							new MediaType("text", "javascript"),
							new MediaType("text", "html")));
			headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

			final HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

			final String betInfoUrl = properties.getExternalTransactionInfoUrl() + "/betinfo";
			final ResponseEntity<String> response = restTemplate.exchange(
					betInfoUrl,
					HttpMethod.POST, requestEntity, String.class);

			if (response.getStatusCodeValue() != 200) {
				final String logEntry = buildLogEntry(requestBody, betInfoUrl, response, requestEntity);

				log.error("requestExternalBetInfo error: " + logEntry);
				return null;
			}

			if (log.isInfoEnabled()) {
				final String logEntry = buildLogEntry(requestBody, betInfoUrl, response, requestEntity);
				log.info("requestExternalBetInfo successful request: " + logEntry);
			}

			try {
				final BetInfoResponse responseJson =
						objectMapper.readValue(response.getBody(), BetInfoResponse.class);

				return responseJson;

			} catch (IOException e) {
				log.error("requestExternalBetInfo - Unable to parse response Json: " + response.getBody(), e);
			}
		}
		log.warn("Property for externalTransactionInfoUrl is missing!");
		return null;
	}

	private String buildLogEntry(String requestBody, String betInfoUrl, ResponseEntity<String> response, HttpEntity<String> requestEntity) {
		final StringBuilder sb = new StringBuilder();
		final Object responseBody = response.hasBody() ?
				response.getBody() : null;

		sb
				.append("\n\n\n")
				.append("==================================================").append("\n")
				.append("==================================================").append("\n")
				.append("--- Request ---").append("\n")
				.append("Url: ").append(betInfoUrl).append("\n")
				.append("Method: POST").append("\n")
				.append("Headers: ").append(requestEntity.getHeaders().toString()).append("\n")
				.append("RequestBody:").append("\n")
				.append(requestBody).append("\n\n")
				.append("==================================================").append("\n")
				.append("--- Response ---").append("\n")
				.append("Status Code: ").append(response.getStatusCodeValue()).append("\n")
				.append("Headers: ").append(response.getHeaders().toString()).append("\n");

		if (responseBody != null) {
			sb
					.append("ResponseBody:").append("\n")
					.append(responseBody.toString()).append("\n");
		} else {
			sb
					.append("ResponseBody:").append("<< No Body Returned >>").append("\n");
		}

		sb
			.append("\n")
			.append("==================================================").append("\n")
			.append("==================================================").append("\n")
			.append("\n");
		return sb.toString();
	}
}
