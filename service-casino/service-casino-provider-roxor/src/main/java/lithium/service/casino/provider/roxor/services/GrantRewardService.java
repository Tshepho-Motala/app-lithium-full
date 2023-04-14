package lithium.service.casino.provider.roxor.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.modules.ModuleInfo;
import lithium.service.Response;
import lithium.service.casino.client.objects.request.AwardBonusRequest;
import lithium.service.casino.client.objects.response.AwardBonusResponse;
import lithium.service.casino.provider.roxor.api.exceptions.Status400BadRequestException;
import lithium.service.casino.provider.roxor.api.exceptions.Status500RuntimeException;
import lithium.service.casino.provider.roxor.api.schema.Duration;
import lithium.service.casino.provider.roxor.api.schema.Money;
import lithium.service.casino.provider.roxor.api.schema.Reward;
import lithium.service.casino.provider.roxor.api.schema.Source;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorObject;
import lithium.service.casino.provider.roxor.api.schema.error.ErrorStatus;
import lithium.service.casino.provider.roxor.api.schema.rewards.GrantRewardRequest;
import lithium.service.casino.provider.roxor.api.schema.rewards.GrantRewardResponse;
import lithium.service.casino.provider.roxor.config.ProviderConfig;
import lithium.service.casino.provider.roxor.config.ProviderConfigService;
import lithium.service.casino.provider.roxor.context.GamePlayContext;
import lithium.service.casino.provider.roxor.storage.entities.RewardBonusMap;
import lithium.service.casino.provider.roxor.storage.repositories.RewardBonusMapRepository;
import lithium.service.casino.provider.roxor.util.ValidationHelper;
import lithium.service.domain.client.objects.Domain;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.util.ExceptionMessageUtil;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class GrantRewardService {
    @Autowired ModuleInfo moduleInfo;
    @Autowired ValidationHelper validationHelper;
    @Autowired ProviderConfigService providerConfigService;
    @Autowired UserApiInternalClientService userApiInternalClientService;
    @Autowired RewardBonusMapRepository rewardBonusMapRepository;

    @Autowired
    public GrantRewardService(@Qualifier("lithium.rest") RestTemplateBuilder builder) {
        this.restTemplate = builder
                .build();
    }
    RestTemplate restTemplate;

    public AwardBonusResponse grantReward(
            AwardBonusRequest awardBonusRequest
    ) throws
            Status400BadRequestException,
            Status500RuntimeException
    {
        try {
            log.info("awardBonus request : " + awardBonusRequest);

            //fetch data to construct roxor request
            Domain domain = validationHelper.getDomain(
                    new GamePlayContext(), awardBonusRequest.getDomainName()
            );
            ProviderConfig pc = providerConfigService.getConfig(
                    moduleInfo.getModuleName(), awardBonusRequest.getDomainName()
            );
            ArrayList<String> roxorGameKeyList = getRoxorGameKeyList(
                    awardBonusRequest.getGames(), awardBonusRequest.getDomainName()
            );
            User user = userApiInternalClientService.getUserByGuid(awardBonusRequest.getUserId());

            RewardBonusMap rewardBonusMap = mapBonus(awardBonusRequest.getExtBonusId());
            Boolean usePlayerApiToken = pc.getUsePlayerApiToken() != null ? pc.getUsePlayerApiToken() : Boolean.TRUE;
            Boolean usePlayerIdFromGuid = pc.getUsePlayerIdFromGuid() != null ? pc.getUsePlayerIdFromGuid() : Boolean.FALSE;

            //construct roxor request
            GrantRewardRequest grantRewardRequest = null;
            switch(awardBonusRequest.getRewardType().toLowerCase()) {

                case "free_spin" :

                    grantRewardRequest = GrantRewardRequest
                            .builder()
                            .reward(Reward
                                    .builder()
                                    .website(pc.getWebsite())
                                    .gameKey(roxorGameKeyList)
                                    .playerId(getRoxorPlayerId(usePlayerApiToken, usePlayerIdFromGuid, user))
                                    .rewardId(rewardBonusMap.getRoxorRewardId())
                                    .rewardType("free_spin") // reward Type possible hardcode or property
                                    .numberOfUnits(awardBonusRequest.getRounds())
                                    .unitValue(Money
                                            .builder()
                                            .currency(domain.getCurrency())
                                            .amount(awardBonusRequest.getRoundValueInCents())
                                            .build())
                                    .duration(Duration
                                            .builder()
                                            .validFrom(Calendar.getInstance().getTimeInMillis())
                                            .expires(getRewardExpiryDate(awardBonusRequest, pc))
                                            .build())
                                    .metadata(null)
                                    .source(Source
                                            .builder()
                                            .sourceType(pc.getWebsite())
                                            .sourceId(rewardBonusMap.getRoxorRewardId())
                                            .build())
                                    .campaign(new Reward.Campaign(awardBonusRequest.getBonusCode()))
                                    .build())
                            .build();
                    break;

                case "instant_reward" :

                    grantRewardRequest = GrantRewardRequest
                            .builder()
                            .reward(Reward
                                    .builder()
                                    .website(pc.getWebsite())
                                    .gameKey(roxorGameKeyList)
                                    .playerId(getRoxorPlayerId(usePlayerApiToken, usePlayerIdFromGuid, user))
                                    .rewardId(rewardBonusMap.getRoxorRewardId())
                                    .rewardType("instant_reward") // reward Type possible hardcode or property
                                    .numberOfUnits(null)
                                    .unitValue(null)
                                    .instantReward(Reward
                                            .InstantReward.
                                            builder()
                                            .remainingUnits(awardBonusRequest.getRounds())
                                            .unitValue(Double.valueOf(awardBonusRequest.getRoundValueInCents()) / 100)
                                            .currency(domain.getCurrency())
                                            .volatility("FIXED")
                                            .build()
                                    )
                                    .duration(Duration
                                            .builder()
                                            .validFrom(Calendar.getInstance().getTimeInMillis())
                                            .expires(getRewardExpiryDate(awardBonusRequest, pc))
                                            .build())
                                    .metadata(null)
                                    .source(Source
                                            .builder()
                                            .sourceType(pc.getWebsite())
                                            .sourceId(rewardBonusMap.getRoxorRewardId())
                                            .build())
                                    .campaign(new Reward.Campaign(awardBonusRequest.getBonusCode()))
                                    .build())
                            .build();
                    break;

                case "bonus_cash" :

                    grantRewardRequest = GrantRewardRequest
                            .builder()
                            .reward(Reward
                                    .builder()
                                    .website(pc.getWebsite())
                                    .gameKey(roxorGameKeyList)
                                    .playerId(getRoxorPlayerId(usePlayerApiToken, usePlayerIdFromGuid, user))
                                    .rewardId(rewardBonusMap.getRoxorRewardId())
                                    .rewardType("bonus_cash") // reward Type possible hardcode or property
                                    .numberOfUnits(null)
                                    .unitValue(null)
                                    .bonusCash(Reward
                                            .BonusCash.
                                            builder()
                                            .initial(Double.valueOf(awardBonusRequest.getRoundValueInCents()) / 100)
                                            .balance(Double.valueOf(awardBonusRequest.getRoundValueInCents()) / 100)
                                            .redeemed(0.00)
                                            .currency(domain.getCurrency())
                                            .build()
                                    )
                                    .duration(Duration
                                            .builder()
                                            .validFrom(Calendar.getInstance().getTimeInMillis())
                                            .expires(getRewardExpiryDate(awardBonusRequest, pc))
                                            .build())
                                    .metadata(null)
                                    .source(Source
                                            .builder()
                                            .sourceType(pc.getWebsite())
                                            .sourceId(rewardBonusMap.getRoxorRewardId())
                                            .build())
                                    .campaign(new Reward.Campaign(awardBonusRequest.getBonusCode()))
                                    .build())
                            .build();
                    break;

            }

            //invoke roxor service
            log.debug("roxor grant reward request : {}", grantRewardRequest);
            GrantRewardResponse grantRewardResponse = executeGrantRewardService(pc.getRewardsUrl(), grantRewardRequest);
            log.debug("roxor grant reward response : {}", grantRewardResponse);

            //convert roxor response to lithium response
            if (grantRewardResponse != null && grantRewardResponse.getErrorStatus() != null) {
                AwardBonusResponse awardBonusResponse = new AwardBonusResponse(-1);
                awardBonusResponse.setErrorCode(grantRewardResponse.getErrorStatus().getError().getCategory());
                awardBonusResponse.setCode(grantRewardResponse.getErrorStatus().getCode());
                awardBonusResponse.setResult(grantRewardResponse.getErrorStatus().getError().getDisplayMessage());
                awardBonusResponse.setDescription(grantRewardResponse.getErrorStatus().getError().getDisplayMessage());
                return awardBonusResponse;
            } else {
                AwardBonusResponse awardBonusResponse = new AwardBonusResponse(Integer.valueOf(awardBonusRequest.getExtBonusId()));
                awardBonusResponse.setResult(Response.Status.OK.id()+"");
                awardBonusResponse.setCode(Response.Status.OK.id()+"");
                log.debug("AwardBonusResponse: " + awardBonusResponse);
                return awardBonusResponse;
            }

        } catch (
                Status400BadRequestException e
        ) {
            log.error("grant-reward-bad-request-exception [request=" + awardBonusRequest + "] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw e;
        } catch (UserNotFoundException e) {
            log.error("grant-reward-user-not-found-exception [request= " + awardBonusRequest + "] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException(e);
        } catch (UserClientServiceFactoryException e) {
            log.error("grant-reward-user-client-service-factory-exception [request=" + awardBonusRequest + "] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException(e);
        } catch (Exception e) {
            log.error("grant-reward-unhandled-exception [request=" + awardBonusRequest + "] " +
                    ExceptionMessageUtil.allMessages(e), e);
            throw new Status500RuntimeException(e);
        }
    }

    private GrantRewardResponse executeGrantRewardService(String rewardHostUrl, GrantRewardRequest rewardRequest) throws Exception {
        if (!rewardHostUrl.endsWith("/")) {
            rewardHostUrl = rewardHostUrl + "/";
        }

        String grantRewardsUrl = rewardHostUrl + "rewards-api/v1/players/" +
                rewardRequest.getReward().getPlayerId() + "/rewards?provider=Gamesys";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<GrantRewardRequest> requestEntity = new HttpEntity<>(rewardRequest,headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(grantRewardsUrl, requestEntity, String.class);
        if (responseEntity != null && responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.hasBody()) {
            ObjectMapper mapper = new ObjectMapper();
            GrantRewardResponse rewardResponse = mapper.readValue(responseEntity.getBody(), GrantRewardResponse.class);
            log.debug("Reward Response Headers : {}",responseEntity.getHeaders());
            log.debug("Reward Response Body : {}",responseEntity.getBody());
            return rewardResponse;
        }

        log.debug("Reward Error Response Headers : {}",responseEntity.getHeaders());
        log.debug("Reward Error Response Body : {}",responseEntity.getBody());
        if (responseEntity.hasBody()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(responseEntity.getBody());
            if (jsonNode.has("status") && jsonNode.path("status").has("error")) {
                String errorCode = jsonNode.path("status").path("code").asText();
                int category = jsonNode.path("status").path("error").path("category").asInt();
                String diagnostic = jsonNode.path("status").path("error").path("diagnostic").asText();

                return GrantRewardResponse.builder()
                        .errorStatus(ErrorStatus.builder()
                                .error(ErrorObject.builder()
                                        .category(category)
                                        .displayMessage(diagnostic)
                                        .build())
                                .code(errorCode)
                                .build())
                        .build();
            }
        }
        throw new IllegalArgumentException("Roxor Reward Error Response : " + responseEntity.getBody());
    }

    private ArrayList<String> getRoxorGameKeyList(String games, String domainName) {
        ArrayList<String> returnList = new ArrayList<>();
        List<String> gameGuidList = Arrays.asList(games.split("\\|").clone());

        String currentGameGuid = "";
        try {
            for (String gameGuid : gameGuidList) {
                currentGameGuid = gameGuid;
                lithium.service.games.client.objects.Game lithiumGame = validationHelper.getGame(
                        new GamePlayContext(),
                        domainName,
                        moduleInfo.getModuleName(),
                        gameGuid
                );

                if (lithiumGame != null) {
                    returnList.add(lithiumGame.getProviderGameId());
                }
            }
        } catch (Exception e) {
            log.error("GrantReward-GameFetch-Exception  [domainName=" + domainName +
                    ", gameList=" + games + ", gameInList=" + currentGameGuid, e);
        }
        return returnList;
    }

    private Long getRewardExpiryDate(AwardBonusRequest awardBonusRequest, ProviderConfig pc) {
        Calendar calendar = Calendar.getInstance();
        if (awardBonusRequest.getExpirationHours() != null) {
            calendar.add(Calendar.HOUR, awardBonusRequest.getExpirationHours());
        } else {
            calendar.add(Calendar.HOUR, pc.getRewardsDefaultDurationInHours());
        }
        return calendar.getTimeInMillis();
    }

    private RewardBonusMap mapBonus(String extBonusId) {
        RewardBonusMap rewardBonusMap = rewardBonusMapRepository.findOrCreateByLithiumExtBonusId(
                Long.valueOf(extBonusId),
                () -> RewardBonusMap.builder()
                        .roxorRewardId(UUID.randomUUID().toString())
                        .build()
        );

        return rewardBonusMap;
    }

    private String getRoxorPlayerId(Boolean usePlayerApiToken, Boolean usePlayerIdFromGuid, User user) {
        if (usePlayerApiToken) {
            return user.getUserApiToken().getToken();
        } else if(usePlayerIdFromGuid) {
            return user.getGuid().split("/")[1];
        } else {
            return user.getId().toString();
        }
    }
}
