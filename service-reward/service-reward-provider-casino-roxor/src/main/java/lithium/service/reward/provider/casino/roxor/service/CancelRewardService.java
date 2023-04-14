package lithium.service.reward.provider.casino.roxor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Optional;

import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.reward.provider.casino.roxor.CancelError;
import lithium.service.reward.provider.casino.roxor.CancelStatus;
import lithium.service.reward.provider.casino.roxor.CancelStatus.Code;
import lithium.service.reward.provider.casino.roxor.config.ProviderConfig;
import lithium.service.reward.provider.casino.roxor.config.ProviderConfigService;
import lithium.service.reward.provider.client.dto.CancelRewardRequest;
import lithium.service.reward.provider.client.dto.CancelRewardResponse;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class CancelRewardService {

  @Autowired
  ModuleInfo moduleInfo;
  @Autowired
  ProviderConfigService providerConfigService;

  @Autowired
  public CancelRewardService(@Qualifier( "lithium.rest" ) RestTemplateBuilder builder) {
    restTemplate = builder.build();

    //Adding this here so that we can use the PATCH request method without errors
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    restTemplate.setRequestFactory(requestFactory);

  }

  RestTemplate restTemplate;

  public CancelRewardResponse cancelReward(CancelRewardRequest request)
  throws Exception
  {
    ProviderConfig pc = providerConfigService.getConfig(moduleInfo.getModuleName(), request.getDomainName());
    log.debug("ProviderConfig: " + pc);

    //invoke roxor service
    log.debug("roxor cancel reward request : {}", request);
    lithium.service.reward.provider.casino.roxor.CancelRewardResponse cancelRewardResponse = executeCancelRewardService(pc.getRewardsUrl(), request.getReferenceId());
    log.debug("roxor cancel reward response : {}", cancelRewardResponse);

    if (cancelRewardResponse.getStatus().getCode() == Code.OK) {
      log.debug("Player reward component with reference {} and id {} was successfully cancelled on Roxor", request.getReferenceId(), request.getPlayerRewardTypeHistoryId());
      return CancelRewardResponse.builder().code(Response.Status.OK.id() + "").result(Response.Status.OK.id() + "").build();
    }

    log.error("Failed to cancel reward component {} due to {}", request.getPlayerRewardTypeHistoryId(), cancelRewardResponse.getStatus().getError().getDiagnostic());

    if (cancelRewardResponse.getStatus().getCode() == Code.CLIENT_ERROR) {
      return CancelRewardResponse.builder()
          .errorCode(Integer.parseInt(cancelRewardResponse.getStatus().getError().getCategory()))
          .result(cancelRewardResponse.getStatus().getError().getDiagnostic())
          .description(cancelRewardResponse.getStatus().getError().getDiagnostic())
          .build();
    }

    return CancelRewardResponse.builder().errorCode(-1)
            .result(cancelRewardResponse.getStatus().getError().getDiagnostic())
            .build();
  }

  private lithium.service.reward.provider.casino.roxor.CancelRewardResponse executeCancelRewardService(String rewardHostUrl, String roxorRewardId)
  throws Exception
  {
    if (!rewardHostUrl.endsWith("/")) {
      rewardHostUrl = rewardHostUrl + "/";
    }

    String cancelRewardUrl = rewardHostUrl + "rewards-api/v1/rewards/cancel?rewardId=" + roxorRewardId;
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<?> requestEntity = new HttpEntity<>(null, headers);

    ResponseEntity<String> responseEntity = restTemplate.exchange(cancelRewardUrl, HttpMethod.PATCH, requestEntity, String.class, new HashMap<>());

    lithium.service.reward.provider.casino.roxor.CancelRewardResponse rewardResponse = null;


    if (responseEntity.hasBody()) {
      try {
        ObjectMapper mapper = new ObjectMapper();
        rewardResponse = mapper.readValue(responseEntity.getBody(), lithium.service.reward.provider.casino.roxor.CancelRewardResponse.class);
      }
      catch (Throwable e) {
        log.warn("Could not transform  roxor cancel reward response", e);
      }

    }

    if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.hasBody()) {
      log.debug("Reward Response Headers : {}", responseEntity.getHeaders());
      log.debug("Reward Response Body : {}", responseEntity.getBody());

    } else {
      log.error("Reward Error Response Headers : {}", responseEntity.getHeaders());
      log.error("Reward Error Response Body : {}", responseEntity.getBody());
    }


    return Optional.ofNullable(rewardResponse)
            .orElse(lithium.service.reward.provider.casino.roxor.CancelRewardResponse.builder()
                    .rewardId(roxorRewardId)
                    .status(CancelStatus.builder()
                            .error(CancelError.builder()
                                    .diagnostic(responseEntity.getBody())
                                    .build())
                            .build())
                    .build());
  }
}
