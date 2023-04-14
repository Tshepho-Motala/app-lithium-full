package lithium.service.reward.provider.casino.roxor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lithium.math.CurrencyAmount;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.reward.client.dto.IRewardTypeName;
import lithium.service.reward.client.dto.RewardRevisionTypeGame;
import lithium.service.reward.provider.casino.roxor.BonusCash;
import lithium.service.reward.provider.casino.roxor.Campaign;
import lithium.service.reward.provider.casino.roxor.Duration;
import lithium.service.reward.provider.casino.roxor.ErrorObject;
import lithium.service.reward.provider.casino.roxor.ErrorStatus;
import lithium.service.reward.provider.casino.roxor.GrantRewardRequest;
import lithium.service.reward.provider.casino.roxor.GrantRewardResponse;
import lithium.service.reward.provider.casino.roxor.Money;
import lithium.service.reward.provider.casino.roxor.Reward;
import lithium.service.reward.provider.casino.roxor.RewardTypeName;
import lithium.service.reward.provider.casino.roxor.Source;
import lithium.service.reward.provider.casino.roxor.config.ProviderConfig;
import lithium.service.reward.provider.casino.roxor.config.ProviderConfigService;
import lithium.service.reward.provider.casino.roxor.RewardTypeFieldName;
import lithium.service.reward.provider.client.dto.ProcessRewardRequest;
import lithium.service.reward.provider.client.dto.ProcessRewardResponse;
import lithium.service.reward.provider.client.dto.ProcessRewardStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ProcessRewardService {

  @Autowired
  ModuleInfo moduleInfo;
  @Autowired
  ProviderConfigService providerConfigService;

  @Autowired
  public ProcessRewardService(@Qualifier( "lithium.rest" ) RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  RestTemplate restTemplate;

  public ProcessRewardResponse processReward(ProcessRewardRequest request)
  throws Exception
  {
    //todo: lookup reward using received reward code.
    // this will reveal the reward types associates with this reward.
    // for each reward type we will go to the respective providers and ask to process.

    ProviderConfig pc = providerConfigService.getConfig(moduleInfo.getModuleName(), request.domainName());
    log.debug("ProviderConfig: " + pc);

    long amountAffectedCents = 0L;
    long amountGiven = 0L;
    long valueInCents = 0L;
    long valueUsed = 0L;

    String campaignId = (request.getReward()!=null)?request.getReward().getId()+"":null;
    GrantRewardRequest grantRewardRequest = null;
    RewardTypeName rewardTypeName = (RewardTypeName) IRewardTypeName.fromRewardTypeName(RewardTypeName.values(), request.getRewardType().getName());

    switch (rewardTypeName) {
      case FREESPIN:
        log.debug("Awarding FREESPINS");
        grantRewardRequest = buildFreeSpinGrantRequest(request, pc, campaignId);
        amountAffectedCents =
            request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class) * request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        amountGiven = request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class);
        valueInCents = request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        break;
      case INSTANT_REWARD:
      case INSTANT_REWARD_FREESPIN:
        log.debug("Awarding INSTANT_REWARDS");
        grantRewardRequest = buildInstantRewardGrantRequest(request, pc, campaignId);
        amountAffectedCents =
            request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class) * request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
            amountGiven = request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class);
            valueInCents = request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        break;
      case CASINO_CHIP:
        log.debug("Awarding CASINO_CHIPS");
        grantRewardRequest = buildCasinoChipGrantRequest(request, pc, campaignId);
        amountAffectedCents = request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);
        valueUsed = amountAffectedCents;
        break;
    }

    //invoke roxor service
    log.debug("roxor grant reward request : {}", grantRewardRequest);
    GrantRewardResponse grantRewardResponse = executeGrantRewardService(pc.getRewardsUrl(), grantRewardRequest);
    log.debug("roxor grant reward response : {}", grantRewardResponse);

    if (grantRewardResponse != null && grantRewardResponse.getErrorStatus() != null) {
      return ProcessRewardResponse.builder()
          .code(grantRewardResponse.getErrorStatus().getCode())
          .result(grantRewardResponse.getErrorStatus().getError().getDisplayMessage())
          .errorCode(grantRewardResponse.getErrorStatus().getError().getCategory())
          .description(grantRewardResponse.getErrorStatus().getError().getDisplayMessage())
          .status(ProcessRewardStatus.FAILED)
          .build();
    } else {
      return ProcessRewardResponse.builder()
          .code(Response.Status.OK.id() + "")
          .result(Response.Status.OK.id() + "")
          .amountAffected(amountAffectedCents)
          .externalReferenceId(grantRewardRequest.getReward().getRewardId())
          .status(ProcessRewardStatus.SUCCESS)
              .valueGiven(amountGiven)
              .valueUsed(valueUsed)
              .valueInCents(valueInCents)
          .build();
    }
    //    return ProcessRewardResponse.builder().status(10).externalReferenceId(UUID.randomUUID().toString()).build();
  }

  private GrantRewardRequest buildInstantRewardGrantRequest(ProcessRewardRequest request, ProviderConfig pc, String campaignId) {
    final String rewardId = UUID.nameUUIDFromBytes(String.format("PLAYER_ROXOR_REWARD_%s", request.getPlayerRewardTypeHistoryId()).getBytes()).toString();
    //construct roxor request
    GrantRewardRequest grantRewardRequest = GrantRewardRequest
        .builder()
        .reward(Reward
            .builder()
            .website(pc.getWebsite())
            .gameKey(request.getRewardRevisionTypeGames().stream().map(this::getProviderGameId).collect(Collectors.toList()))
            .playerId(Optional.ofNullable(pc.getUsePlayerApiToken()).orElse(false) ? request.getPlayer().getApiToken()
                : request.getPlayer().getOriginalId())
            .rewardId(rewardId)
            .rewardType(RewardTypeName.INSTANT_REWARD.rgpName())
            .instantReward(Reward.InstantReward.builder().volatility("FIXED").currency(request.domainCurrency()).remainingUnits(request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class)).unitValue(
                CurrencyAmount.fromCents(request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class)).toAmount()
            ).build())
            .duration(Duration
                .builder()
                .validFrom(Calendar.getInstance().getTimeInMillis())
                .expires(getRewardExpiryDate(request, pc))
                .build())
            .metadata(null)
            .source(Source
                .builder()
                .sourceType(pc.getWebsite())
                .sourceId(rewardId)
                .build())
            .build())
        .build();

    if (campaignId != null) {
      grantRewardRequest.getReward().setCampaign(Campaign
          .builder()
          .campaignId(campaignId)
          .build()
      );
    }

    return grantRewardRequest;
  }

  private GrantRewardRequest buildFreeSpinGrantRequest(ProcessRewardRequest request, ProviderConfig pc, String campaignId) {
    //construct roxor request
    final String rewardId = UUID.nameUUIDFromBytes(String.format("PLAYER_ROXOR_REWARD_%s", request.getPlayerRewardTypeHistoryId()).getBytes()).toString();
    GrantRewardRequest grantRewardRequest = GrantRewardRequest.builder()
        .reward(Reward.builder()
            .website(pc.getWebsite())
            .gameKey(request.getRewardRevisionTypeGames().stream().map(this::getProviderGameId).collect(Collectors.toList()))
            .playerId(Optional.ofNullable(pc.getUsePlayerApiToken()).orElse(false) ? request.getPlayer().getApiToken()
                : request.getPlayer().getOriginalId())
            //            .rewardId(request.getPlayerRewardTypeHistoryId().toString())
            .rewardId(rewardId)
            .rewardType(RewardTypeName.FREESPIN.rgpName())
            .numberOfUnits(request.findRewardTypeValue(RewardTypeFieldName.NUMBER_OF_ROUNDS.getName(), Integer.class))
            .unitValue(
                Money.builder().currency(request.domainCurrency()).amount(request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class)).build())
            .duration(Duration.builder().validFrom(Calendar.getInstance().getTimeInMillis()).expires(getRewardExpiryDate(request, pc)).build())
            .metadata(null)
            .source(Source.builder().sourceType(pc.getWebsite()).sourceId(rewardId).build())
            .build())
        .build();

    if (campaignId != null) {
      grantRewardRequest.getReward().setCampaign(Campaign.builder().campaignId(campaignId).build());
    }

    return grantRewardRequest;
  }

  private GrantRewardRequest buildCasinoChipGrantRequest(ProcessRewardRequest request, ProviderConfig pc, String campaignId) {

    final String rewardId = UUID.nameUUIDFromBytes(String.format("PLAYER_ROXOR_REWARD_%s", request.getPlayerRewardTypeHistoryId()).getBytes()).toString();
    Long roundValueInCents = request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class);

    GrantRewardRequest grantRewardRequest = GrantRewardRequest.builder()
            .reward(Reward.builder()
                    .website(pc.getWebsite())
                    .gameKey(request.getRewardRevisionTypeGames().stream().map(this::getProviderGameId).collect(Collectors.toList()))
                    .playerId(Optional.ofNullable(pc.getUsePlayerApiToken()).orElse(false) ? request.getPlayer().getApiToken()
                            : request.getPlayer().getOriginalId())
                    .rewardId(rewardId)
                    .rewardType(RewardTypeName.CASINO_CHIP.rgpName())
                    .numberOfUnits(0)
                    .unitValue(Money.builder()
                            .currency(request.domainCurrency())
                            .amount(request.findRewardTypeValue(RewardTypeFieldName.ROUND_VALUE_IN_CENTS.getName(), Long.class))
                            .build())
                    .duration(Duration.builder()
                            .validFrom(Calendar.getInstance()
                            .getTimeInMillis())
                            .expires(getRewardExpiryDate(request, pc))
                            .build())
                    .metadata(null)
                    .source(Source.builder().sourceType(pc.getWebsite()).sourceId(rewardId).build())
                    .bonusCash(BonusCash.builder()
                            .initial(BigDecimal.valueOf(roundValueInCents / 100))
                            .balance(BigDecimal.valueOf(roundValueInCents / 100))
                            .redeemed(BigDecimal.ZERO)
                            .currency(request.domainCurrency())
                            .build())
                    .build())
            .build();

    if (campaignId != null) {
      grantRewardRequest.getReward().setCampaign(Campaign.builder().campaignId(campaignId).build());
    }

    return grantRewardRequest;
  }

  private GrantRewardResponse executeGrantRewardService(String rewardHostUrl, GrantRewardRequest rewardRequest)
  throws Exception
  {
    if (!rewardHostUrl.endsWith("/")) {
      rewardHostUrl = rewardHostUrl + "/";
    }

    String grantRewardsUrl = rewardHostUrl + "rewards-api/v1/players/" + rewardRequest.getReward().getPlayerId() + "/rewards?provider=Gamesys";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
    headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
    HttpEntity<GrantRewardRequest> requestEntity = new HttpEntity<>(rewardRequest, headers);

    ResponseEntity<String> responseEntity = restTemplate.postForEntity(grantRewardsUrl, requestEntity, String.class);
    if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.hasBody()) {
      ObjectMapper mapper = new ObjectMapper();
      GrantRewardResponse rewardResponse = mapper.readValue(responseEntity.getBody(), GrantRewardResponse.class);
      log.debug("Reward Response Headers : {}", responseEntity.getHeaders());
      log.debug("Reward Response Body : {}", responseEntity.getBody());
      return rewardResponse;
    }

    log.debug("Reward Error Response Headers : {}", responseEntity.getHeaders());
    log.debug("Reward Error Response Body : {}", responseEntity.getBody());
    if (responseEntity.hasBody()) {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.readTree(responseEntity.getBody());
      if (jsonNode.has("status") && jsonNode.path("status").has("error")) {
        String errorCode = jsonNode.path("status").path("code").asText();
        int category = jsonNode.path("status").path("error").path("category").asInt();
        String diagnostic = jsonNode.path("status").path("error").path("diagnostic").asText();

        return GrantRewardResponse.builder()
            .errorStatus(
                ErrorStatus.builder().error(ErrorObject.builder().category(category).displayMessage(diagnostic).build()).code(errorCode).build())
            .build();
      }
    }
    throw new IllegalArgumentException("Roxor Reward Error Response : " + responseEntity.getBody());
  }

  private Long getRewardExpiryDate(ProcessRewardRequest request, ProviderConfig pc) {
    Calendar calendar = Calendar.getInstance();
    if ((request.getRewardRevision().getValidFor() != null) && (request.getRewardRevision().getValidForGranularity() != null)) {
      switch (request.getRewardRevision().getValidForGranularity()) {
        case GRANULARITY_HOUR:
          calendar.add(Calendar.HOUR, request.getRewardRevision().getValidFor());
          break;
        case GRANULARITY_DAY:
          calendar.add(Calendar.DAY_OF_YEAR, request.getRewardRevision().getValidFor());
          break;
        case GRANULARITY_WEEK:
          calendar.add(Calendar.WEEK_OF_YEAR, request.getRewardRevision().getValidFor());
          break;
        case GRANULARITY_MONTH:
          calendar.add(Calendar.MONTH, request.getRewardRevision().getValidFor());
          break;
        case GRANULARITY_YEAR:
          calendar.add(Calendar.YEAR, request.getRewardRevision().getValidFor());
          break;
        default:
          calendar.add(Calendar.HOUR, pc.getRewardsDefaultDurationInHours());
      }
    } else {
      calendar.add(Calendar.HOUR, pc.getRewardsDefaultDurationInHours());
    }
    return calendar.getTimeInMillis();
  }

  private String getProviderGameId(RewardRevisionTypeGame rewardRevisionTypeGame) {
    //GameGuid will be in this form [service-casino-provider_play-banghai] but roxor will only recognise play_banghai as a gamekey
    String gameId = rewardRevisionTypeGame.getGameId();

    //We have introduced a gameId property on RewardRevisionTypeGame but we need to cater for previous rewards which will have a null value for gameId
    if (gameId == null) {
      gameId = rewardRevisionTypeGame.getGuid().toLowerCase()
              .replace("service-casino-provider-roxor_", "");
    }

    return gameId;
  }
}
